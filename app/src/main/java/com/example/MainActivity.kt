package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.ActiveDialog
import com.example.ui.GameViewModel
import com.example.ui.screens.CharacterCreationScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.dialogs.BuildGuideDialog
import com.example.ui.dialogs.SaveLoadDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TheLostKingdomApp()
                }
            }
        }
    }
}

@Composable
fun TheLostKingdomApp(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.currentScreen) {
        AppScreen.MAIN_MENU -> {
            MainMenuScreen(
                hasExistingSave = uiState.hasExistingSave,
                isMuted = uiState.isAudioMuted,
                onStartNewGame = { viewModel.startNewGameFlow() },
                onContinueGame = { viewModel.continueGame() },
                onOpenSaves = { viewModel.openDialog(ActiveDialog.SAVE_LOAD) },
                onOpenGuide = { viewModel.openDialog(ActiveDialog.BUILD_GUIDE) },
                onToggleAudio = { viewModel.toggleAudio() }
            )

            // Modals accessible from Main Menu
            if (uiState.activeDialog == ActiveDialog.SAVE_LOAD) {
                SaveLoadDialog(
                    slots = uiState.saveSlots,
                    onDismiss = { viewModel.closeDialog() },
                    onSaveToSlot = { viewModel.saveGameToSlot(it) },
                    onLoadFromSlot = {
                        viewModel.loadGameFromSlot(it)
                        viewModel.continueGame()
                    },
                    onExportSlot = { viewModel.exportSlot(it) },
                    onImportSlot = { slot, json -> viewModel.importSlot(slot, json) }
                )
            } else if (uiState.activeDialog == ActiveDialog.BUILD_GUIDE) {
                BuildGuideDialog(onDismiss = { viewModel.closeDialog() })
            }
        }

        AppScreen.CHARACTER_CREATION -> {
            CharacterCreationScreen(
                onBack = { viewModel.backToMainMenu() },
                onStartAdventure = { name, heroClass ->
                    viewModel.createHeroAndStart(name, heroClass)
                }
            )
        }

        AppScreen.GAMEPLAY -> {
            GameScreen(
                viewModel = viewModel,
                onBackToMenu = { viewModel.backToMainMenu() }
            )
        }
    }
}
