package com.example.personalisednavigationassistant.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalisednavigationassistant.viewmodel.TourUiState
import com.example.personalisednavigationassistant.viewmodel.TourViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TourViewModel, onOpenCameraClick: () -> Unit) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Αρχαίος Ξεναγός") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Σέβεται τον χώρο της μπάρας τίτλου
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ScanButton(
                text = "Σκανάρισμα Μνημείου",
                enabled = uiState !is TourUiState.Loading,
                onClick = onOpenCameraClick // <--- ΑΛΛΑΞΕ ΑΥΤΟ!
            )

            Spacer(modifier = Modifier.height(32.dp))


            when (val state = uiState) {
                is TourUiState.Idle -> {
                    Text(
                        text = "Καλώς ήρθατε! Πατήστε το κουμπί για να ξεκινήσετε την ξενάγηση.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is TourUiState.Loading -> {
                    LoadingView(message = "Ο AI Ξεναγός ετοιμάζει την ιστορία...")
                }
                is TourUiState.Success -> {
                    StoryCard(story = state.story)
                }
                is TourUiState.Error -> {
                    Text(
                        text = "Σφάλμα: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}