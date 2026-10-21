package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Quest
import com.example.model.QuestStatus

@Composable
fun QuestLogDialog(
    quests: List<Quest>,
    onDismiss: () -> Unit,
    onTrackQuest: (Quest) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Available", "Completed")

    val filteredQuests = when (selectedTab) {
        0 -> quests.filter { it.status == QuestStatus.ACTIVE }
        1 -> quests.filter { it.status == QuestStatus.AVAILABLE }
        else -> quests.filter { it.status == QuestStatus.COMPLETED }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
                .border(2.dp, Color(0xFF415A77), RoundedCornerShape(20.dp))
                .testTag("quest_log_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Aldoria Quest Chronicles",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Hero missions, bounties, and rewards",
                            color = Color(0xFF9E9E9E),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_quests")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0D1B2A),
                    contentColor = Color(0xFFFFD700),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFFD700)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = "$title (${when (index) {
                                        0 -> quests.count { it.status == QuestStatus.ACTIVE }
                                        1 -> quests.count { it.status == QuestStatus.AVAILABLE }
                                        else -> quests.count { it.status == QuestStatus.COMPLETED }
                                    }})",
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quests List
                if (filteredQuests.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = when (selectedTab) {
                                0 -> "No active quests. Speak to Elder Rowan or check Available quests!"
                                1 -> "All available quests have been taken! Check back after clearing dungeons."
                                else -> "No completed quests yet. Finish tasks to forge your legend!"
                            },
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredQuests) { quest ->
                            QuestCard(quest = quest, onTrack = { onTrackQuest(quest) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestCard(quest: Quest, onTrack: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E2D42),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (quest.isReadyToTurnIn) Color(0xFFFFD700) else Color(0xFF2E3D52), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quest.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Giver: ${quest.giverName}",
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(quest.description, color = Color(0xFFB0BEC5), fontSize = 11.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Objective & Progress
            Text(
                text = "Target: ${quest.targetObjective}",
                color = Color(0xFF81C784),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )

            if (quest.status == QuestStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { (quest.currentProgress.toFloat() / quest.targetProgress).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (quest.isReadyToTurnIn) Color(0xFFFFD700) else Color(0xFF2ECC71),
                        trackColor = Color(0xFF0D1B2A)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${quest.currentProgress}/${quest.targetProgress}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rewards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${quest.rewardGold}g", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(Icons.Default.Star, contentDescription = "EXP", tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${quest.rewardExp} EXP", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (quest.status == QuestStatus.ACTIVE) {
                    Button(
                        onClick = onTrack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (quest.isReadyToTurnIn) Color(0xFFFF9F43) else Color(0xFF3498DB)
                        ),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = if (quest.isReadyToTurnIn) "Ready to Return!" else "Track",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (quest.status == QuestStatus.COMPLETED) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color(0xFF2ECC71), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Completed", color = Color(0xFF2ECC71), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
