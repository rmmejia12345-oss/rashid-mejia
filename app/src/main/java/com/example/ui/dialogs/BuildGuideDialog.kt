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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun BuildGuideDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .border(2.dp, Color(0xFF00D2D3), RoundedCornerShape(20.dp))
                .testTag("build_guide_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Android, contentDescription = "Android", tint = Color(0xFF3DDC84), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("The Lost Kingdom Manual", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("Android Build & Architecture Guide", color = Color(0xFF3DDC84), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_guide")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // APK Status Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3A2B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.5.dp, Color(0xFF2ECC71), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Ready", tint = Color(0xFF2ECC71), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CAN AN APK BE BUILT HERE? YES!", color = Color(0xFF2ECC71), fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Unlike generic chat sessions, Google AI Studio runs a complete native Android build toolchain (Gradle 9, Android SDK 36, Kotlin 2.2, Jetpack Compose). Every time the code changes, it compiles into a real Android APK running live on your streaming emulator!",
                            color = Color(0xFFE8F8F5),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // How to export
                Text("HOW TO EXPORT & INSTALL ON YOUR PHONE", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                GuideStepRow(
                    step = "1",
                    title = "Download APK / AAB",
                    desc = "Open the AI Studio project settings menu (top right) and click 'Export / Generate APK' to directly download the signed package onto your Android phone or tablet."
                )
                GuideStepRow(
                    step = "2",
                    title = "Export Source Code ZIP",
                    desc = "Select 'Export Project ZIP' or 'Push to GitHub' to obtain all Kotlin files, Gradle build scripts, AndroidManifest.xml, and launcher assets."
                )
                GuideStepRow(
                    step = "3",
                    title = "Open in Android Studio",
                    desc = "If you prefer local development, open the extracted folder directly in Android Studio. Run `gradle assembleDebug` to test on connected physical hardware."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Game Features Included
                Text("GAMEPLAY & ENGINE ARCHITECTURE", color = Color(0xFF00D2D3), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                FeaturePoint("🎮 3D Perspective Canvas Engine", "60 FPS hardware-accelerated Canvas with camera tracking, depth sorting, dynamic drop shadows, and 2.5D elevation.")
                FeaturePoint("⚔ 4 Character Classes", "Warrior (Whirlwind, Shield Guard), Mage (Arcane Missiles, Frost Nova), Archer (Piercing Snipe, Volley), Healer (Divine Blessing, Sunburst).")
                FeaturePoint("👹 Combat & Boss Battles", "Real-time attacks, directional dodging, enemy AI telegraph warning circles, damage floating numbers, and multi-phase bosses.")
                FeaturePoint("📜 Quests & Story Dialogues", "The Lost Crown of Aldoria main questline, side bounties, interactive NPC dialogues with Elder Rowan, Blacksmith Bryan, Princess Lyra.")
                FeaturePoint("🎒 Inventory & Blacksmith Forge", "Weapons, armor, potions, crystals, and item enhancement (+1 to +5) at Bryan's forge.")
                FeaturePoint("🌐 Online Adventurers Guild", "Recruit online companion heroes to party, receive active co-op stat auras, complete bounties, and climb the Global Hall of Fame.")
                FeaturePoint("💾 Multi-Slot Save System", "3 persistent device save slots with timestamps and JSON cloud export/import.")
                FeaturePoint("🔊 Pure Audio Synthesis", "Zero missing audio files! Real-time AudioTrack waveform synthesis for swings, spells, level ups, coins, and haptic vibration.")

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun GuideStepRow(step: String, title: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E2D42),
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF00D2D3), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(step, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(desc, color = Color(0xFFB0BEC5), fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun FeaturePoint(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(title, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(desc, color = Color(0xFFECEFF1), fontSize = 11.sp)
    }
}
