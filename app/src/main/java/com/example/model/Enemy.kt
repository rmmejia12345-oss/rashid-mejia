package com.example.model

import androidx.compose.ui.graphics.Color

enum class EnemyType(
    val displayName: String,
    val baseHp: Int,
    val baseAttack: Int,
    val baseDefense: Int,
    val moveSpeed: Float,
    val attackRange: Float,
    val attackCooldownSec: Float,
    val primaryColor: Color,
    val secondaryColor: Color,
    val isBoss: Boolean = false,
    val expReward: Int = 30,
    val goldReward: Int = 20
) {
    FOREST_SLIME(
        displayName = "Forest Slime",
        baseHp = 60,
        baseAttack = 12,
        baseDefense = 3,
        moveSpeed = 1.3f,
        attackRange = 45f,
        attackCooldownSec = 1.8f,
        primaryColor = Color(0xFF2ECC71),
        secondaryColor = Color(0xFF27AE60),
        isBoss = false,
        expReward = 25,
        goldReward = 15
    ),
    SPROUT_SPRITE(
        displayName = "Sprout Sprite",
        baseHp = 45,
        baseAttack = 15,
        baseDefense = 2,
        moveSpeed = 1.7f,
        attackRange = 130f,
        attackCooldownSec = 2.2f,
        primaryColor = Color(0xFFA8E6CF),
        secondaryColor = Color(0xFF1ABC9C),
        isBoss = false,
        expReward = 30,
        goldReward = 20
    ),
    CAVE_BAT(
        displayName = "Crystal Bat",
        baseHp = 55,
        baseAttack = 16,
        baseDefense = 4,
        moveSpeed = 2.2f,
        attackRange = 50f,
        attackCooldownSec = 1.4f,
        primaryColor = Color(0xFF8E44AD),
        secondaryColor = Color(0xFF34495E),
        isBoss = false,
        expReward = 35,
        goldReward = 25
    ),
    CRYSTAL_GOLEM(
        displayName = "Crystal Sentinel",
        baseHp = 140,
        baseAttack = 24,
        baseDefense = 14,
        moveSpeed = 0.9f,
        attackRange = 60f,
        attackCooldownSec = 2.4f,
        primaryColor = Color(0xFF00CED1),
        secondaryColor = Color(0xFF2C3E50),
        isBoss = false,
        expReward = 60,
        goldReward = 45
    ),
    BOSS_SLIME_KING(
        displayName = "King Slime Royale",
        baseHp = 380,
        baseAttack = 28,
        baseDefense = 12,
        moveSpeed = 1.1f,
        attackRange = 80f,
        attackCooldownSec = 2.0f,
        primaryColor = Color(0xFF00B894),
        secondaryColor = Color(0xFFFFD700),
        isBoss = true,
        expReward = 150,
        goldReward = 120
    ),
    BOSS_ANCIENT_GOLEM(
        displayName = "Ancient Citadel Titan",
        baseHp = 650,
        baseAttack = 38,
        baseDefense = 22,
        moveSpeed = 0.8f,
        attackRange = 90f,
        attackCooldownSec = 2.5f,
        primaryColor = Color(0xFFE17055),
        secondaryColor = Color(0xFF6C5CE7),
        isBoss = true,
        expReward = 350,
        goldReward = 300
    )
}

data class Enemy(
    val id: String,
    val type: EnemyType,
    var x: Float,
    var y: Float,
    var currentHp: Int = type.baseHp,
    val maxHp: Int = type.baseHp,
    var attackTimer: Float = 0f,
    var telegraphProgress: Float = 0f, // 0f to 1f warning circle before attack lands
    var isTelegraphing: Boolean = false,
    var state: EnemyState = EnemyState.IDLE,
    var targetX: Float = x,
    var targetY: Float = y,
    var hitFlashTimer: Float = 0f
) {
    val isAlive: Boolean
        get() = currentHp > 0
}

enum class EnemyState {
    IDLE,
    CHASING,
    TELEGRAPHING,
    ATTACKING,
    HURT
}
