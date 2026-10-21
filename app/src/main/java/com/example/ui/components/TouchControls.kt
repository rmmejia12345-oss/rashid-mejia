package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hero
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun TouchControls(
    hero: Hero,
    skill1CooldownRemaining: Float,
    skill2CooldownRemaining: Float,
    isInteractingAvailable: Boolean,
    interactionLabel: String,
    onJoystickMove: (Float, Float) -> Unit,
    onAttackClick: () -> Unit,
    onSkill1Click: () -> Unit,
    onSkill2Click: () -> Unit,
    onDodgeClick: () -> Unit,
    onInteractClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Left Side: Virtual Analog Joystick
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 28.dp)
                .size(150.dp)
                .testTag("joystick_touch_area"),
            contentAlignment = Alignment.Center
        ) {
            var knobOffsetX by remember { mutableFloatStateOf(0f) }
            var knobOffsetY by remember { mutableFloatStateOf(0f) }
            val maxRadiusPx = 110f

            // Outer Base Ring
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x33000000), Color(0x77000000), Color(0xAA111111))
                        )
                    )
                    .border(2.dp, Color(0x66FFFFFF), CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                knobOffsetX = 0f
                                knobOffsetY = 0f
                                onJoystickMove(0f, 0f)
                            },
                            onDragCancel = {
                                knobOffsetX = 0f
                                knobOffsetY = 0f
                                onJoystickMove(0f, 0f)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newX = knobOffsetX + dragAmount.x
                                val newY = knobOffsetY + dragAmount.y
                                val dist = hypot(newX, newY)
                                if (dist <= maxRadiusPx) {
                                    knobOffsetX = newX
                                    knobOffsetY = newY
                                } else {
                                    knobOffsetX = (newX / dist) * maxRadiusPx
                                    knobOffsetY = (newY / dist) * maxRadiusPx
                                }
                                onJoystickMove(knobOffsetX / maxRadiusPx, knobOffsetY / maxRadiusPx)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Directional Arrows
                Text("▲", color = Color(0x55FFFFFF), fontSize = 12.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp))
                Text("▼", color = Color(0x55FFFFFF), fontSize = 12.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp))
                Text("◀", color = Color(0x55FFFFFF), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp))
                Text("▶", color = Color(0x55FFFFFF), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp))

                // Draggable Center Knob
                Box(
                    modifier = Modifier
                        .offset { IntOffset(knobOffsetX.roundToInt(), knobOffsetY.roundToInt()) }
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(hero.heroClass.accentColor, hero.heroClass.primaryColor)
                            )
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    )
                }
            }
        }

        // Right Side: Action Cluster
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .testTag("combat_actions_cluster")
        ) {
            Column(horizontalAlignment = Alignment.End) {
                // Secondary row (Interact & Dodge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Context Action (Talk / Open Chest)
                    if (isInteractingAvailable) {
                        Surface(
                            onClick = onInteractClick,
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFF39C12),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("btn_interact")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                Icon(
                                    imageVector = if (interactionLabel.contains("Talk")) Icons.Default.Chat else Icons.Default.LockOpen,
                                    contentDescription = interactionLabel,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = interactionLabel,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    // Dodge Roll / Dash Button
                    Surface(
                        onClick = onDodgeClick,
                        shape = CircleShape,
                        color = Color(0xFF34495E),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("btn_dodge")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Dodge Dash",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary combat skills row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Skill 1
                    SkillButton(
                        name = hero.heroClass.skill1Name,
                        cooldownRemaining = skill1CooldownRemaining,
                        totalCooldown = hero.heroClass.skill1CooldownSec,
                        mpCost = hero.heroClass.skill1MpCost,
                        accentColor = hero.heroClass.primaryColor,
                        icon = Icons.Default.FlashOn,
                        tag = "btn_skill_1",
                        onClick = onSkill1Click
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Skill 2
                    SkillButton(
                        name = hero.heroClass.skill2Name,
                        cooldownRemaining = skill2CooldownRemaining,
                        totalCooldown = hero.heroClass.skill2CooldownSec,
                        mpCost = hero.heroClass.skill2MpCost,
                        accentColor = hero.heroClass.accentColor,
                        icon = Icons.Default.Shield,
                        tag = "btn_skill_2",
                        onClick = onSkill2Click
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Basic Attack Button (Large Primary)
                    Surface(
                        onClick = onAttackClick,
                        shape = CircleShape,
                        color = Color(0xFFC0392B),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(76.dp)
                            .testTag("btn_attack")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color(0xFFE74C3C), Color(0xFF962D22))
                                    )
                                )
                                .border(3.dp, Color(0xFFFFD700), CircleShape)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.SportsKabaddi,
                                    contentDescription = "Attack",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                                Text(
                                    text = "ATK",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillButton(
    name: String,
    cooldownRemaining: Float,
    totalCooldown: Float,
    mpCost: Int,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    val isOnCooldown = cooldownRemaining > 0f

    Surface(
        onClick = onClick,
        enabled = !isOnCooldown,
        shape = CircleShape,
        color = if (isOnCooldown) Color(0xFF2C3E50) else accentColor,
        shadowElevation = 5.dp,
        modifier = Modifier
            .size(56.dp)
            .testTag(tag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.border(2.dp, Color(0x88FFFFFF), CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isOnCooldown) Color(0x66FFFFFF) else Color.White,
                modifier = Modifier.size(26.dp)
            )

            // MP Cost Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 2.dp)
                    .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "${mpCost}MP",
                    color = Color(0xFF5DADE2),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isOnCooldown) {
                CircularProgressIndicator(
                    progress = { (cooldownRemaining / totalCooldown).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFD700),
                    strokeWidth = 3.dp,
                )
                Text(
                    text = String.format("%.1f", cooldownRemaining),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
