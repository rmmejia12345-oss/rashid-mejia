package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.engine.GameRenderer3D
import com.example.model.QuestStatus
import com.example.ui.ActiveDialog
import com.example.ui.GameViewModel
import com.example.ui.components.GameHud
import com.example.ui.components.TouchControls
import com.example.ui.dialogs.BlacksmithDialog
import com.example.ui.dialogs.BuildGuideDialog
import com.example.ui.dialogs.CharacterStatsDialog
import com.example.ui.dialogs.InventoryDialog
import com.example.ui.dialogs.NpcDialogueDialog
import com.example.ui.dialogs.OnlineGuildDialog
import com.example.ui.dialogs.QuestLogDialog
import com.example.ui.dialogs.SaveLoadDialog

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle back button: dismiss dialog if open, else return to menu
    BackHandler {
        if (uiState.activeDialog != ActiveDialog.NONE) {
            viewModel.closeDialog()
        } else {
            onBackToMenu()
        }
    }

    val activeQuest = uiState.quests.firstOrNull { it.status == QuestStatus.ACTIVE }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("game_screen_root")
    ) {
        // 1. Hardware Accelerated 3D Perspective Canvas World
        Canvas(modifier = Modifier.fillMaxSize().testTag("game_canvas_3d")) {
            GameRenderer3D.renderWorld(
                drawScope = this,
                engine = viewModel.worldEngine,
                hero = uiState.hero,
                screenWidth = size.width,
                screenHeight = size.height
            )
        }

        // 2. Heads-Up Display (Stats, Currency, Quest ticker, Menu buttons, Potion belt)
        GameHud(
            hero = uiState.hero,
            currentZone = viewModel.worldEngine.currentZone,
            activeQuest = activeQuest,
            isMuted = uiState.isAudioMuted,
            onOpenInventory = { viewModel.openDialog(ActiveDialog.INVENTORY) },
            onOpenStats = { viewModel.openDialog(ActiveDialog.CHARACTER_STATS) },
            onOpenQuests = { viewModel.openDialog(ActiveDialog.QUEST_LOG) },
            onOpenGuild = { viewModel.openDialog(ActiveDialog.ONLINE_GUILD) },
            onOpenSaveLoad = { viewModel.openDialog(ActiveDialog.SAVE_LOAD) },
            onOpenBuildGuide = { viewModel.openDialog(ActiveDialog.BUILD_GUIDE) },
            onToggleAudio = { viewModel.toggleAudio() },
            onQuickHealthPotion = { viewModel.onQuickHealthPotion() },
            onQuickManaPotion = { viewModel.onQuickManaPotion() }
        )

        // 3. Dual Touch Controls (Analog thumbstick + Combat cluster)
        TouchControls(
            hero = uiState.hero,
            skill1CooldownRemaining = viewModel.worldEngine.skill1CooldownTimer,
            skill2CooldownRemaining = viewModel.worldEngine.skill2CooldownTimer,
            isInteractingAvailable = uiState.isInteractingAvailable,
            interactionLabel = uiState.interactionLabel,
            onJoystickMove = { x, y -> viewModel.onJoystickMove(x, y) },
            onAttackClick = { viewModel.onAttackClick() },
            onSkill1Click = { viewModel.onSkill1Click() },
            onSkill2Click = { viewModel.onSkill2Click() },
            onDodgeClick = { viewModel.onDodgeClick() },
            onInteractClick = { viewModel.onInteractClick() }
        )

        // 4. Modal Dialogs
        when (uiState.activeDialog) {
            ActiveDialog.INVENTORY -> {
                InventoryDialog(
                    hero = uiState.hero,
                    onDismiss = { viewModel.closeDialog() },
                    onEquipItem = { viewModel.equipItem(it) },
                    onUseItem = { viewModel.useItem(it) },
                    onSellItem = { viewModel.sellItem(it) }
                )
            }
            ActiveDialog.CHARACTER_STATS -> {
                CharacterStatsDialog(
                    hero = uiState.hero,
                    onDismiss = { viewModel.closeDialog() },
                    onAllocatePoint = { viewModel.allocateStatPoint(it) }
                )
            }
            ActiveDialog.QUEST_LOG -> {
                QuestLogDialog(
                    quests = uiState.quests,
                    onDismiss = { viewModel.closeDialog() },
                    onTrackQuest = { viewModel.closeDialog() }
                )
            }
            ActiveDialog.BLACKSMITH -> {
                BlacksmithDialog(
                    hero = uiState.hero,
                    onDismiss = { viewModel.closeDialog() },
                    onUpgradeItem = { viewModel.upgradeGear(it) }
                )
            }
            ActiveDialog.ONLINE_GUILD -> {
                OnlineGuildDialog(
                    hero = uiState.hero,
                    partyMembers = uiState.partyMembers,
                    allAdventurers = uiState.allAdventurers,
                    bounties = uiState.bounties,
                    leaderboard = uiState.leaderboard,
                    onDismiss = { viewModel.closeDialog() },
                    onTogglePartyMember = { viewModel.togglePartyMember(it) },
                    onClaimBounty = { viewModel.claimBounty(it) }
                )
            }
            ActiveDialog.SAVE_LOAD -> {
                SaveLoadDialog(
                    slots = uiState.saveSlots,
                    onDismiss = { viewModel.closeDialog() },
                    onSaveToSlot = { viewModel.saveGameToSlot(it) },
                    onLoadFromSlot = { viewModel.loadGameFromSlot(it) },
                    onExportSlot = { viewModel.exportSlot(it) },
                    onImportSlot = { slot, json -> viewModel.importSlot(slot, json) }
                )
            }
            ActiveDialog.NPC_DIALOGUE -> {
                uiState.activeDialogueNpc?.let { npc ->
                    val associatedQuest = uiState.quests.firstOrNull { it.id == npc.associatedQuestId }
                    NpcDialogueDialog(
                        npc = npc,
                        associatedQuest = associatedQuest,
                        onDismiss = { viewModel.closeDialog() },
                        onAcceptQuest = { viewModel.acceptQuest(it) },
                        onCompleteQuest = { viewModel.completeQuest(it) },
                        onOpenForge = { viewModel.openDialog(ActiveDialog.BLACKSMITH) },
                        onOpenGuild = { viewModel.openDialog(ActiveDialog.ONLINE_GUILD) }
                    )
                }
            }
            ActiveDialog.BUILD_GUIDE -> {
                BuildGuideDialog(onDismiss = { viewModel.closeDialog() })
            }
            ActiveDialog.NONE -> {
                // No dialog open
            }
        }
    }
}
