package com.example.personalisednavigationassistant.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.personalisednavigationassistant.viewmodel.TourViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import android.content.Intent
import android.net.Uri
import android.content.ActivityNotFoundException
import androidx.compose.ui.platform.LocalContext



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonumentDetailScreen(
    viewModel: TourViewModel,
    monumentId: String,
    monumentName: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showChatBottomSheet by remember { mutableStateOf(false) }
    val currentMonument = monumentsList.find { it.id == monumentId }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = monumentName,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // ΚΟΥΜΠΙ CHAT
                    IconButton(onClick = { showChatBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Chat with AI",
                            tint = Color(0xFF0038FD)
                        )
                    }


                    IconButton(onClick = { /* Μελλοντική χρήση */ }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video",
                            tint = Color.Red
                        )
                    }


                    IconButton(onClick = {
                        if (currentMonument != null) {
                            val uri = Uri.parse("geo:${currentMonument.latitude},${currentMonument.longitude}?q=${currentMonument.latitude},${currentMonument.longitude}(${currentMonument.name})")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                            mapIntent.setPackage("com.google.android.apps.maps")

                            try {
                                context.startActivity(mapIntent)
                            } catch (e: ActivityNotFoundException) {
                                val browserUri = Uri.parse("http://maps.google.com/maps?q=${currentMonument.latitude},${currentMonument.longitude}")
                                val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                                context.startActivity(browserIntent)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Map",
                            tint = Color(0xFF4CAF50)
                        )
                    }
                }

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()
                )
                .animateContentSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {

                AsyncImage(
                    model = currentMonument?.imageRes,
                    contentDescription = monumentName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(modifier = Modifier.padding(16.dp)) {
                if (currentMonument != null) {
                    Text(
                        text = currentMonument.shortDescription,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "💡 ${currentMonument.nextPoi}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text("Οι πληροφορίες για αυτό το μνημείο δεν βρέθηκαν.")
                }
            }

            if (showChatBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showChatBottomSheet = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    AnimatedVisibility(
                        visible = showChatBottomSheet,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }) // Γλιστράει από κάτω και κάνει fade
                    ) {
                        ChatSection(
                            poiId = monumentId,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxHeight(0.8f)
                        )
                    }
                }
            }
        }
    }
}
