package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.MapNpc
import com.example.model.Quest
import com.example.model.QuestStatus

@Composable
fun NpcDialogueDialog(
    npc: MapNpc,
    associatedQuest: Quest?,
    onDismiss: () -> Unit,
    onAcceptQuest: (Quest) -> Unit,
    onCompleteQuest: (Quest) -> Unit,
    onOpenForge: () -> Unit,
    onOpenGuild: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF415A77), RoundedCornerShape(20.dp))
                .testTag("npc_dialogue_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with NPC avatar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(npc.robeColor)
                                .border(2.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = npc.name.first().toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = npc.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = npc.title,
                                color = Color(0xFFFFD54F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_dialogue")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Speech Bubble
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D42)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF2E3D52), RoundedCornerShape(12.dp))
                ) {
                    val dialogueText = when {
                        associatedQuest?.isReadyToTurnIn == true -> associatedQuest.dialogueCompleted
                        associatedQuest?.status == QuestStatus.AVAILABLE -> associatedQuest.dialogueIntro
                        associatedQuest?.status == QuestStatus.ACTIVE -> "How fares your mission, hero? Return to me when you've accomplished the task!"
                        else -> "Peace and prosperity be with you, adventurer. May your blade strike true across Aldoria."
                    }

                    Text(
                        text = "\"$dialogueText\"",
                        color = Color(0xFFE0E0E0),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quest Banner if present
                if (associatedQuest != null && associatedQuest.status != QuestStatus.COMPLETED) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0D1B2A),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF39C12), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Quest: ${associatedQuest.title}",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Objective: ${associatedQuest.targetObjective} (${associatedQuest.currentProgress}/${associatedQuest.targetProgress})",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Reward: ", color = Color.Gray, fontSize = 10.sp)
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                                Text("${associatedQuest.rewardGold}g ", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.Star, contentDescription = "EXP", tint = Color(0xFF81C784), modifier = Modifier.size(13.dp))
                                Text("${associatedQuest.rewardExp} EXP", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (npc.id == "npc_bryan") {
                        Button(
                            onClick = onOpenForge,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                            modifier = Modifier.testTag("btn_npc_open_forge")
                        ) {
                            Text("Open Forge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (npc.id == "npc_vane") {
                        Button(
                            onClick = onOpenGuild,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B59B6)),
                            modifier = Modifier.testTag("btn_npc_open_guild")
                        ) {
                            Text("Guild Hall", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (associatedQuest?.isReadyToTurnIn == true) {
                        Button(
                            onClick = { onCompleteQuest(associatedQuest) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                            modifier = Modifier.testTag("btn_npc_complete_quest")
                        ) {
                            Text("Complete Quest! 🎉", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (associatedQuest?.status == QuestStatus.AVAILABLE) {
                        Button(
                            onClick = { onAcceptQuest(associatedQuest) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                            modifier = Modifier.testTag("btn_npc_accept_quest")
                        ) {
                            Text("Accept Quest", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Goodbye", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
