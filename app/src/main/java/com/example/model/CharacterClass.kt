package com.example.model

import androidx.compose.ui.graphics.Color

enum class CharacterClass(
    val title: String,
    val description: String,
    val baseHp: Int,
    val baseMp: Int,
    val baseStr: Int,
    val baseInt: Int,
    val baseAgi: Int,
    val baseVit: Int,
    val primaryColor: Color,
    val accentColor: Color,
    val weaponName: String,
    val skill1Name: String,
    val skill1Desc: String,
    val skill1CooldownSec: Float,
    val skill1MpCost: Int,
    val skill2Name: String,
    val skill2Desc: String,
    val skill2CooldownSec: Float,
    val skill2MpCost: Int
) {
    WARRIOR(
        title = "Warrior",
        description = "Mighty protector of Aldoria, skilled in close-quarters blade combat and crushing defense.",
        baseHp = 220,
        baseMp = 60,
        baseStr = 16,
        baseInt = 6,
        baseAgi = 10,
        baseVit = 16,
        primaryColor = Color(0xFFC0392B), // Bold Crimson
        accentColor = Color(0xFFF39C12),  // Gold
        weaponName = "Steel Broadsword",
        skill1Name = "Whirlwind Slash",
        skill1Desc = "Spins blade 360° dealing 180% damage to all surrounding enemies.",
        skill1CooldownSec = 4f,
        skill1MpCost = 15,
        skill2Name = "Iron Bulwark",
        skill2Desc = "Raises golden shield, cutting incoming damage by 65% for 5 seconds.",
        skill2CooldownSec = 9f,
        skill2MpCost = 20
    ),
    MAGE(
        title = "Mage",
        description = "Master of ancient arcane energies and frost elemental magic to decimate groups from afar.",
        baseHp = 140,
        baseMp = 180,
        baseStr = 6,
        baseInt = 18,
        baseAgi = 9,
        baseVit = 8,
        primaryColor = Color(0xFF2980B9), // Arcane Azure
        accentColor = Color(0xFF9B59B6),  // Mystic Purple
        weaponName = "Crystal Stave",
        skill1Name = "Arcane Missiles",
        skill1Desc = "Fires a rapid barrage of three homing magical bolts towards target.",
        skill1CooldownSec = 3f,
        skill1MpCost = 20,
        skill2Name = "Frost Nova",
        skill2Desc = "Freezes ground in a large radius, damaging and slowing enemies by 50%.",
        skill2CooldownSec = 7f,
        skill2MpCost = 35
    ),
    ARCHER(
        title = "Archer",
        description = "Swift ranger from the Whispering Woods, striking critical hits with pinpoint bow precision.",
        baseHp = 160,
        baseMp = 90,
        baseStr = 11,
        baseInt = 8,
        baseAgi = 18,
        baseVit = 10,
        primaryColor = Color(0xFF27AE60), // Forest Emerald
        accentColor = Color(0xFFE67E22),  // Amber
        weaponName = "Elven Composite Bow",
        skill1Name = "Piercing Snipe",
        skill1Desc = "Shoots a high-velocity piercing arrow that passes through all enemies in line.",
        skill1CooldownSec = 3.5f,
        skill1MpCost = 15,
        skill2Name = "Arrow Volley",
        skill2Desc = "Rains a deadly shower of arrows onto targeted area dealing continuous damage.",
        skill2CooldownSec = 6.5f,
        skill2MpCost = 25
    ),
    HEALER(
        title = "Healer",
        description = "Devout cleric channeling divine radiance to restore allies and vanquish shadows.",
        baseHp = 175,
        baseMp = 150,
        baseStr = 8,
        baseInt = 15,
        baseAgi = 8,
        baseVit = 14,
        primaryColor = Color(0xFFD4AC0D), // Sacred Gold
        accentColor = Color(0xFF1ABC9C),  // Celestial Turquoise
        weaponName = "Radiant Scepter",
        skill1Name = "Divine Blessing",
        skill1Desc = "Immediately restores 35% of max HP and cures harmful debuffs.",
        skill1CooldownSec = 6f,
        skill1MpCost = 30,
        skill2Name = "Sunburst Blast",
        skill2Desc = "Unleashes a blinding beam of sacred light dealing massive radiant damage.",
        skill2CooldownSec = 5f,
        skill2MpCost = 25
    )
}
