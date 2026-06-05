package com.example.personalisednavigationassistant.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.example.personalisednavigationassistant.viewmodel.TourViewModel
import androidx.compose.material3.CircularProgressIndicator // Μην ξεχάσεις το import
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSection(
    poiId: String,
    viewModel: TourViewModel,
    modifier: Modifier = Modifier

) {
    var inputText by remember { mutableStateOf("") }


    val messages by viewModel.chatMessages.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->

                if (message.text == "Σκέφτομαι...") {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    ChatBubble(message)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ρώτα κάτι για το μνημείο...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(poiId = poiId, message = inputText)
                                inputText = "" // Καθαρίζουμε το πεδίο
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Gray)
                    }
                }
            )
        }
    }
}


@Composable
fun ChatBubble(message: TourViewModel.ChatMessage) {
    val isUser = message.isUser
    val backgroundColor = if (isUser) Color(0xFF00E5FF) else Color(0xFFE0E0E0)
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = Color.Black
            )
        }
    }
}