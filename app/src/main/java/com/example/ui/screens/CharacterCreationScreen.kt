package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharacterClass

@Composable
fun CharacterCreationScreen(
    onBack: () -> Unit,
    onStartAdventure: (String, CharacterClass) -> Unit,
    modifier: Modifier = Modifier
) {
    var heroName by remember { mutableStateOf("Sir Cedric") }
    var selectedClass by remember { mutableStateOf(CharacterClass.WARRIOR) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D131F))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_character_creation")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Create Your Hero",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Choose your name, class, and combat specialty",
                        color = Color(0xFF9E9E9E),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Name Field
            Text("HERO NAME", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = heroName,
                onValueChange = { if (it.length <= 16) heroName = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_hero_name"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E2D42),
                    unfocusedContainerColor = Color(0xFF141E30)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Class Selection Grid
            Text("SELECT CHARACTER CLASS", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CharacterClass.values().forEach { cClass ->
                    val isSelected = selectedClass == cClass
                    Surface(
                        onClick = { selectedClass = cClass },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF2C3E50) else Color(0xFF141E30),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) cClass.primaryColor else Color(0xFF2E3D52),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("btn_select_class_${cClass.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(cClass.primaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cClass.title.first().toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cClass.title,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Class Overview Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D42)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, selectedClass.primaryColor, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(selectedClass.accentColor, selectedClass.primaryColor)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(selectedClass.title.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(selectedClass.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Starter Weapon: ${selectedClass.weaponName}", color = Color(0xFFFFD54F), fontSize = 11.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = selectedClass.primaryColor.copy(alpha = 0.3f),
                            modifier = Modifier.border(1.dp, selectedClass.primaryColor, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Base HP: ${selectedClass.baseHp}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(selectedClass.description, color = Color(0xFFB0BEC5), fontSize = 12.sp, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Base Stat Meters
                    Text("STARTING ATTRIBUTE PROFILE", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    StatBar("Strength", selectedClass.baseStr, 20, Color(0xFFFF7675))
                    StatBar("Intellect", selectedClass.baseInt, 20, Color(0xFF74B9FF))
                    StatBar("Agility", selectedClass.baseAgi, 20, Color(0xFF55EFC4))
                    StatBar("Vitality", selectedClass.baseVit, 20, Color(0xFFFFD54F))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Skills
                    Text("SIGNATURE COMBAT SKILLS", color = Color(0xFF00D2D3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    SkillPreviewRow(selectedClass.skill1Name, selectedClass.skill1Desc, Icons.Default.FlashOn)
                    Spacer(modifier = Modifier.height(4.dp))
                    SkillPreviewRow(selectedClass.skill2Name, selectedClass.skill2Desc, Icons.Default.Shield)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Journey Button
            Button(
                onClick = {
                    val finalName = if (heroName.isNotBlank()) heroName.trim() else "Hero Cedric"
                    onStartAdventure(finalName, selectedClass)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_embark_adventure")
            ) {
                Icon(Icons.Default.Check, contentDescription = "Start", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Embark to Oakhaven", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatBar(label: String, value: Int, maxValue: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.LightGray, fontSize = 10.sp, modifier = Modifier.width(68.dp))
        LinearProgressIndicator(
            progress = { (value.toFloat() / maxValue).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF0D1B2A)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("$value", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
    }
}

@Composable
private fun SkillPreviewRow(name: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1B2A), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = name, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(desc, color = Color.LightGray, fontSize = 10.sp)
        }
    }
}
