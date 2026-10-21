package com.example.model

import androidx.compose.ui.graphics.Color

data class OnlineAdventurer(
    val id: String,
    val name: String,
    val heroClass: CharacterClass,
    val level: Int,
    val guildTitle: String,
    val status: String,
    val avatarColor: Color,
    val isInParty: Boolean = false,
    val attackBonus: Int = 10,
    val defenseBonus: Int = 8
)

data class GuildBounty(
    val id: String,
    val title: String,
    val targetDescription: String,
    val goldReward: Int,
    val gemReward: Int,
    val isCompleted: Boolean = false
)

data class LeaderboardEntry(
    val rank: Int,
    val heroName: String,
    val heroClass: CharacterClass,
    val level: Int,
    val bossTrophies: Int,
    val dungeonClears: Int
)

object DefaultGuildData {
    fun createAdventurers(): List<OnlineAdventurer> = listOf(
        OnlineAdventurer(
            id = "adv_1",
            name = "Lady Valerie",
            heroClass = CharacterClass.WARRIOR,
            level = 7,
            guildTitle = "Vanguard of Oakhaven",
            status = "Ready for Dungeon Raid",
            avatarColor = Color(0xFFC0392B),
            isInParty = false,
            attackBonus = 18,
            defenseBonus = 15
        ),
        OnlineAdventurer(
            id = "adv_2",
            name = "Archmage Elidor",
            heroClass = CharacterClass.MAGE,
            level = 8,
            guildTitle = "Mystic Scholar",
            status = "Brewing potions at the tavern",
            avatarColor = Color(0xFF2980B9),
            isInParty = false,
            attackBonus = 22,
            defenseBonus = 6
        ),
        OnlineAdventurer(
            id = "adv_3",
            name = "Robin of the Glen",
            heroClass = CharacterClass.ARCHER,
            level = 6,
            guildTitle = "Pathfinder",
            status = "Scouting Whispering Woods",
            avatarColor = Color(0xFF27AE60),
            isInParty = false,
            attackBonus = 16,
            defenseBonus = 9
        ),
        OnlineAdventurer(
            id = "adv_4",
            name = "Brother Alistair",
            heroClass = CharacterClass.HEALER,
            level = 9,
            guildTitle = "High Priest of Sol",
            status = "Healing pilgrims",
            avatarColor = Color(0xFFF1C40F),
            isInParty = false,
            attackBonus = 12,
            defenseBonus = 14
        )
    )

    fun createBounties(): List<GuildBounty> = listOf(
        GuildBounty(
            id = "bounty_1",
            title = "Forest Cleanup",
            targetDescription = "Defeat 8 Forest Slimes in Whispering Woods",
            goldReward = 150,
            gemReward = 5
        ),
        GuildBounty(
            id = "bounty_2",
            title = "Crystal Harvester",
            targetDescription = "Collect 5 Astral Crystal Shards from the Caverns",
            goldReward = 220,
            gemReward = 10
        ),
        GuildBounty(
            id = "bounty_3",
            title = "Citadel Vanguard",
            targetDescription = "Conquer the Ancient Citadel Titan Boss",
            goldReward = 500,
            gemReward = 25
        )
    )

    fun createLeaderboard(): List<LeaderboardEntry> = listOf(
        LeaderboardEntry(1, "Aethelgard", CharacterClass.WARRIOR, 20, 18, 42),
        LeaderboardEntry(2, "Seraphina", CharacterClass.MAGE, 19, 15, 38),
        LeaderboardEntry(3, "SilverArrow", CharacterClass.ARCHER, 18, 14, 35),
        LeaderboardEntry(4, "DawnBringer", CharacterClass.HEALER, 17, 12, 30),
        LeaderboardEntry(5, "GarethTheBrave", CharacterClass.WARRIOR, 15, 10, 24)
    )
}
