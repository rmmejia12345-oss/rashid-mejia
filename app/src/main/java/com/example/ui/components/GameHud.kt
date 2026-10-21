package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import com.example.model.Hero
import com.example.model.Quest
import com.example.model.ZoneType

@Composable
fun GameHud(
    hero: Hero,
    currentZone: ZoneType,
    activeQuest: Quest?,
    isMuted: Boolean,
    onOpenInventory: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenQuests: () -> Unit,
    onOpenGuild: () -> Unit,
    onOpenSaveLoad: () -> Unit,
    onOpenBuildGuide: () -> Unit,
    onToggleAudio: () -> Unit,
    onQuickHealthPotion: () -> Unit,
    onQuickManaPotion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hpPotionsCount = hero.inventory.firstOrNull { it.id == "c_hp_potion" }?.quantity ?: 0
    val mpPotionsCount = hero.inventory.firstOrNull { it.id == "c_mp_potion" }?.quantity ?: 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // TOP ROW: Hero Card (Left) & Menu Action Cluster (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left: Hero Profile Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xDD1B263B),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .border(1.5.dp, Color(0xFF415A77), RoundedCornerShape(16.dp))
                    .clickable { onOpenStats() }
                    .testTag("hero_hud_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    // Class Avatar Icon
                    Box(
                        modifier = Modifier
                            .size(46.dp)
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
                            fontSize = 22.sp
                        )
                        // Level Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .background(Color(0xFFE74C3C), CircleShape)
                                .padding(2.dp)
                        ) {
                            Text(
                                text = "${hero.level}",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = hero.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = hero.heroClass.title,
                                color = hero.heroClass.accentColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // HP Bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("HP", color = Color(0xFFE74C3C), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            LinearProgressIndicator(
                                progress = { (hero.currentHp.toFloat() / hero.maxHp).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .width(84.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFFE74C3C),
                                trackColor = Color(0xFF4A1515)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${hero.currentHp}/${hero.maxHp}", color = Color.White, fontSize = 9.sp)
                        }

                        // MP Bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("MP", color = Color(0xFF3498DB), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            LinearProgressIndicator(
                                progress = { (hero.currentMp.toFloat() / hero.maxMp).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .width(84.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF3498DB),
                                trackColor = Color(0xFF152A4A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${hero.currentMp}/${hero.maxMp}", color = Color.White, fontSize = 9.sp)
                        }
                    }
                }
            }

            // Right: HUD Buttons & Currency
            Column(horizontalAlignment = Alignment.End) {
                // Gold & Gems Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xDD0D1B2A),
                    modifier = Modifier.border(1.dp, Color(0xFF415A77), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Gold",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${hero.gold}",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Gems",
                            tint = Color(0xFF00D2D3),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${hero.gems}",
                            color = Color(0xFF00D2D3),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Menu Icons Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Audio Mute Toggle
                    HudIconButton(
                        icon = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        desc = "Audio Toggle",
                        tag = "btn_hud_audio",
                        onClick = onToggleAudio
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Build Guide & Manual
                    HudIconButton(
                        icon = Icons.Default.HelpOutline,
                        desc = "Build Guide",
                        tag = "btn_hud_guide",
                        tint = Color(0xFF00D2D3),
                        onClick = onOpenBuildGuide
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Online Adventurers Guild
                    HudIconButton(
                        icon = Icons.Default.Group,
                        desc = "Adventurers Guild",
                        tag = "btn_hud_guild",
                        tint = Color(0xFF9B59B6),
                        onClick = onOpenGuild
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Quest Log
                    HudIconButton(
                        icon = Icons.Default.Bookmark,
                        desc = "Quests",
                        tag = "btn_hud_quests",
                        tint = Color(0xFFF39C12),
                        onClick = onOpenQuests
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Inventory
                    HudIconButton(
                        icon = Icons.Default.Backpack,
                        desc = "Inventory",
                        tag = "btn_hud_inventory",
                        tint = Color(0xFF2ECC71),
                        onClick = onOpenInventory
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Stats / Character Sheet
                    if (hero.unspentStatPoints > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFFE74C3C)) {
                                    Text("${hero.unspentStatPoints}")
                                }
                            }
                        ) {
                            HudIconButton(
                                icon = Icons.Default.Person,
                                desc = "Character Stats",
                                tag = "btn_hud_stats",
                                tint = Color(0xFF3498DB),
                                onClick = onOpenStats
                            )
                        }
                    } else {
                        HudIconButton(
                            icon = Icons.Default.Person,
                            desc = "Character Stats",
                            tag = "btn_hud_stats",
                            tint = Color(0xFF3498DB),
                            onClick = onOpenStats
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Save / Load
                    HudIconButton(
                        icon = Icons.Default.Save,
                        desc = "Save Load",
                        tag = "btn_hud_save",
                        tint = Color.White,
                        onClick = onOpenSaveLoad
                    )
                }
            }
        }

        // TOP-CENTER: Zone Banner & Active Quest Ticker
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 68.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xBB000000),
                modifier = Modifier.border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = "📍 ${currentZone.title}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }

            if (activeQuest != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xCC1A1A24),
                    modifier = Modifier
                        .border(1.dp, Color(0xFFF39C12), RoundedCornerShape(10.dp))
                        .clickable { onOpenQuests() }
                ) {
                    Text(
                        text = "⚔ Quest: ${activeQuest.title} (${activeQuest.currentProgress}/${activeQuest.targetProgress})",
                        color = Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // BOTTOM-LEFT: Quick Potion Belt (Above the joystick area)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 180.dp, y = (-28).dp)
        ) {
            // Quick HP Potion
            Surface(
                onClick = onQuickHealthPotion,
                shape = RoundedCornerShape(14.dp),
                color = Color(0xDD962D22),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(46.dp)
                    .border(1.5.dp, Color(0xFFE74C3C), RoundedCornerShape(14.dp))
                    .testTag("btn_quick_hp_potion")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocalPharmacy,
                        contentDescription = "Drink HP Potion",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$hpPotionsCount",
                        color = Color(0xFFFFEB3B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick MP Potion
            Surface(
                onClick = onQuickManaPotion,
                shape = RoundedCornerShape(14.dp),
                color = Color(0xDD1B4F72),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(46.dp)
                    .border(1.5.dp, Color(0xFF3498DB), RoundedCornerShape(14.dp))
                    .testTag("btn_quick_mp_potion")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Drink MP Potion",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$mpPotionsCount",
                        color = Color(0xFF85C1E9),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HudIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    desc: String,
    tag: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color(0xDD0D1B2A),
        shadowElevation = 4.dp,
        modifier = Modifier
            .size(38.dp)
            .border(1.dp, Color(0xFF415A77), CircleShape)
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = desc,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
