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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Hero

@Composable
fun CharacterStatsDialog(
    hero: Hero,
    onDismiss: () -> Unit,
    onAllocatePoint: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
                .border(2.dp, Color(0xFF415A77), RoundedCornerShape(20.dp))
                .testTag("character_stats_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hero Attributes & Progression",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Train skills & allocate attribute points",
                            color = Color(0xFF9E9E9E),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_stats")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Identity Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E2D42),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF2E3D52), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(hero.heroClass.accentColor, hero.heroClass.primaryColor)
                                    )
                                )
                                .border(2.dp, Color(0xFFFFD700), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hero.heroClass.title.first().toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hero.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Level ${hero.level}",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "${hero.heroClass.title} • Realm of Aldoria",
                                color = hero.heroClass.accentColor,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            // EXP Bar
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("EXP", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                LinearProgressIndicator(
                                    progress = { (hero.exp.toFloat() / hero.expToNextLevel).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF81C784),
                                    trackColor = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${hero.exp}/${hero.expToNextLevel}", color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Unspent Points Banner
                if (hero.unspentStatPoints > 0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF962D22),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✨ ${hero.unspentStatPoints} Unspent Attribute Points Available! Assign below:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Attributes List
                Text(
                    text = "CORE ATTRIBUTES",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                AttributeRow(
                    label = "Strength (STR)",
                    desc = "Increases Physical Attack & Cleave damage",
                    value = hero.statStrength,
                    canAdd = hero.unspentStatPoints > 0,
                    onAdd = { onAllocatePoint("STR") }
                )
                AttributeRow(
                    label = "Intellect (INT)",
                    desc = "Boosts Magic Spell power & Max Mana",
                    value = hero.statIntellect,
                    canAdd = hero.unspentStatPoints > 0,
                    onAdd = { onAllocatePoint("INT") }
                )
                AttributeRow(
                    label = "Agility (AGI)",
                    desc = "Raises Movement Speed & Critical Hit Chance",
                    value = hero.statAgility,
                    canAdd = hero.unspentStatPoints > 0,
                    onAdd = { onAllocatePoint("AGI") }
                )
                AttributeRow(
                    label = "Vitality (VIT)",
                    desc = "Maximizes Health Pool & Base Armor Defense",
                    value = hero.statVitality,
                    canAdd = hero.unspentStatPoints > 0,
                    onAdd = { onAllocatePoint("VIT") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Derived Combat Stats
                Text(
                    text = "DERIVED COMBAT STATISTICS",
                    color = Color(0xFF00D2D3),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("⚔ Total Attack Power:", color = Color.LightGray, fontSize = 11.sp)
                            Text("${hero.totalAttack}", color = Color(0xFFFF7675), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("🛡 Total Physical Defense:", color = Color.LightGray, fontSize = 11.sp)
                            Text("${hero.totalDefense}", color = Color(0xFF74B9FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("🎯 Critical Strike Chance:", color = Color.LightGray, fontSize = 11.sp)
                            Text("${String.format("%.1f", hero.critChance * 100)}%", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("⚡ Movement Speed:", color = Color.LightGray, fontSize = 11.sp)
                            Text("${String.format("%.0f", hero.moveSpeedMultiplier * 100)}%", color = Color(0xFF55EFC4), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Skill Mastery Information
                Text(
                    text = "CLASS SKILL MASTERY",
                    color = Color(0xFF9B59B6),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                SkillInfoRow(
                    skillName = hero.heroClass.skill1Name,
                    desc = hero.heroClass.skill1Desc,
                    level = hero.skill1Level,
                    icon = Icons.Default.FlashOn
                )
                Spacer(modifier = Modifier.height(4.dp))
                SkillInfoRow(
                    skillName = hero.heroClass.skill2Name,
                    desc = hero.heroClass.skill2Desc,
                    level = hero.skill2Level,
                    icon = Icons.Default.Shield
                )
            }
        }
    }
}

@Composable
private fun AttributeRow(
    label: String,
    desc: String,
    value: Int,
    canAdd: Boolean,
    onAdd: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0D1B2A),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .border(1.dp, Color(0xFF2E3D52), RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(desc, color = Color.Gray, fontSize = 10.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$value",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (canAdd) {
                    Surface(
                        onClick = onAdd,
                        shape = CircleShape,
                        color = Color(0xFF2ECC71),
                        modifier = Modifier.size(28.dp).testTag("btn_add_stat_$label")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Increase $label", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillInfoRow(
    skillName: String,
    desc: String,
    level: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E2D42),
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = skillName, tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(skillName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tier $level", color = Color(0xFF5DADE2), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(desc, color = Color.LightGray, fontSize = 10.sp)
            }
        }
    }
}
