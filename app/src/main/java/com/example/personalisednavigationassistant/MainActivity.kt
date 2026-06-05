package com.example.personalisednavigationassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.personalisednavigationassistant.ui.theme.PersonalisedNavigationAssistantTheme
import com.example.personalisednavigationassistant.viewmodel.TourViewModel
import com.example.personalisednavigationassistant.ui.theme.AppNavigation

class MainActivity : ComponentActivity() {

    private val viewModel: TourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalisedNavigationAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}