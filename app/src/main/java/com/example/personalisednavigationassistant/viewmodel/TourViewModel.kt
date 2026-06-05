package com.example.personalisednavigationassistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalisednavigationassistant.data.TourRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Οι 4 πιθανές Καταστάσεις του UI (UI State)
sealed class TourUiState {
    object Idle : TourUiState()                              // Δεν έχει γίνει τίποτα ακόμα
    object Loading : TourUiState()                           // Περιμένουμε το AI
    data class Success(val story: String) : TourUiState()    // Ήρθε η ιστορία!
    data class Error(val message: String) : TourUiState()    // Κάτι πήγε στραβά
}

// 2. Το ViewModel
class TourViewModel : ViewModel() {

    private val repository = TourRepository()

    // Αυτός είναι ο πομπός μας. Το UI θα τον "ακούει" συνεχώς.
    private val _uiState = MutableStateFlow<TourUiState>(TourUiState.Idle)
    val uiState: StateFlow<TourUiState> = _uiState.asStateFlow()

    fun scanMonument(poiId: String) {
        // --- ΝΕΟΣ ΚΩΔΙΚΑΣ: Έλεγχος Εγκυρότητας (Validation) ---
        // Έστω ότι όλα τα δικά μας QR Codes ξεκινούν με κάποιες συγκεκριμένες λέξεις
        val validPrefixes = listOf("parthenon_", "statue_", "temple_", "museum_")
        val isValid = validPrefixes.any { prefix -> poiId.startsWith(prefix) }

        if (!isValid) {
            // Αν είναι άκυρο (π.χ. link από site), βγάζουμε σφάλμα χωρίς να ενοχλήσουμε το n8n
            _uiState.value = TourUiState.Error("Άκυρο QR Code. Σκανάρετε το ταμπελάκι ενός μνημείου.")
            return
        }
        // --------------------------------------------------------

        // Αν είναι έγκυρο, συνεχίζουμε κανονικά:
        _uiState.value = TourUiState.Loading

        viewModelScope.launch {
            val result = repository.fetchStory(poiId = poiId, language = "Greek")

            result.fold(
                onSuccess = { storyText ->
                    _uiState.value = TourUiState.Success(storyText)
                },
                onFailure = { error ->
                    _uiState.value = TourUiState.Error(error.localizedMessage ?: "Άγνωστο σφάλμα δικτύου")
                }
            )
        }
    }

    // Αν θέλουμε να καθαρίσουμε την οθόνη και να γυρίσουμε στην αρχή
    fun resetState() {
        _uiState.value = TourUiState.Idle
    }

    // 1. Το μοντέλο του μηνύματος (αν το έχεις ήδη στο ChatComponent, φέρ' το εδώ)
    data class ChatMessage(val text: String, val isUser: Boolean)

    // 2. Η λίστα που κρατάει όλο το ιστορικό του Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // 3. Η συνάρτηση που στέλνει το μήνυμα στο AI
    fun sendChatMessage(poiId: String, message: String) {
        // 1. Προσθέτουμε την ερώτηση του χρήστη (Γαλάζιο συννεφάκι)
        val userMsg = ChatMessage(text = message, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        // 2. Προσθέτουμε ένα προσωρινό "Loading..." από το AI (Γκρι συννεφάκι)
        val loadingMsg = ChatMessage(text = "Σκέφτομαι...", isUser = false)
        _chatMessages.value = _chatMessages.value + loadingMsg

        // 3. ΕΔΩ ΕΙΝΑΙ Η ΓΡΑΜΜΗ ΠΟΥ ΡΩΤΗΣΕΣ! Στέλνουμε το αίτημα στο n8n
        viewModelScope.launch {
            val result = repository.fetchStory(poiId = poiId, language = "Greek", userMessage = message)

            result.fold(
                onSuccess = { aiResponse ->
                    // Αν πετύχει: Σβήνουμε το "Σκέφτομαι..." (δηλαδή το τελευταίο μήνυμα) και βάζουμε την απάντηση
                    val updatedList = _chatMessages.value.dropLast(1) + ChatMessage(text = aiResponse, isUser = false)
                    _chatMessages.value = updatedList
                },
                onFailure = { error ->
                    // Αν αποτύχει (π.χ. έπεσε το ίντερνετ): Βγάζουμε μήνυμα σφάλματος
                    val updatedList = _chatMessages.value.dropLast(1) + ChatMessage(text = "Σφάλμα: ${error.localizedMessage}", isUser = false)
                    _chatMessages.value = updatedList
                }
            )
        }
    }

    // 4. Συνάρτηση για να καθαρίζουμε το chat όταν αλλάζουμε μνημείο
    fun clearChat() {
        _chatMessages.value = emptyList()
    }

}

