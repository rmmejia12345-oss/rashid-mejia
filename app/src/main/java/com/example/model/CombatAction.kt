package com.example.model

import androidx.compose.ui.graphics.Color

data class DamageNumber(
    val id: Long,
    val value: Int,
    val x: Float,
    var y: Float,
    val isCritical: Boolean = false,
    val isHeal: Boolean = false,
    val isBlocked: Boolean = false,
    var alpha: Float = 1.0f,
    var lifetime: Float = 0.8f
)

data class Projectile(
    val id: Long,
    var x: Float,
    var y: Float,
    val targetX: Float,
    val targetY: Float,
    val speed: Float,
    val damage: Int,
    val isPlayerOwned: Boolean,
    val color: Color,
    val radius: Float = 8f,
    val isPiercing: Boolean = false,
    var distanceTraveled: Float = 0f,
    val maxDistance: Float = 420f
)

data class VisualParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1.0f,
    var life: Float = 0.5f,
    val maxLife: Float = 0.5f
)

data class SlashArc(
    val id: Long,
    val x: Float,
    val y: Float,
    val angle: Float,
    val radius: Float,
    val color: Color,
    var progress: Float = 0f,
    val duration: Float = 0.2f
)
