package com.example.model

enum class QuestStatus {
    AVAILABLE,
    ACTIVE,
    COMPLETED
}

enum class QuestCategory {
    MAIN_STORY,
    SIDE_QUEST,
    GUILD_BOUNTY
}

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val category: QuestCategory,
    val giverName: String,
    val targetObjective: String,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val rewardGold: Int = 50,
    val rewardExp: Int = 80,
    val rewardItem: Item? = null,
    val status: QuestStatus = QuestStatus.AVAILABLE,
    val dialogueIntro: String = "",
    val dialogueCompleted: String = ""
) {
    val isReadyToTurnIn: Boolean
        get() = currentProgress >= targetProgress && status == QuestStatus.ACTIVE
}

object DefaultQuests {
    fun createQuestList(): List<Quest> = listOf(
        Quest(
            id = "mq_1_slime_troubles",
            title = "1. Forest Outskirts",
            description = "Elder Rowan reports playful Forest Slimes have strayed onto the trade road. Clear them to ensure safe travel for merchants.",
            category = QuestCategory.MAIN_STORY,
            giverName = "Elder Rowan",
            targetObjective = "Defeat Forest Slimes in Whispering Woods",
            currentProgress = 0,
            targetProgress = 4,
            rewardGold = 75,
            rewardExp = 90,
            rewardItem = DefaultItems.createHealthPotion(2),
            status = QuestStatus.ACTIVE,
            dialogueIntro = "Greetings, brave traveler! Welcome to Oakhaven. Our kingdom has been troubled since the Royal Crown went missing. Could you start by clearing the pesky slimes in the Whispering Woods?",
            dialogueCompleted = "Splendid work! The road is safe once again. You carry the mark of a true Aldorian hero!"
        ),
        Quest(
            id = "mq_2_crystal_core",
            title = "2. The Glowing Caverns",
            description = "Venture into the Crystal Caverns and retrieve Astral Crystals to illuminate the path to the Sunken Citadel.",
            category = QuestCategory.MAIN_STORY,
            giverName = "Blacksmith Bryan",
            targetObjective = "Gather Astral Crystals or defeat Crystal Bats",
            currentProgress = 0,
            targetProgress = 3,
            rewardGold = 120,
            rewardExp = 160,
            rewardItem = DefaultItems.createCrystalOre(3),
            status = QuestStatus.AVAILABLE,
            dialogueIntro = "Ho there! If you want your steel honed to perfection, I'll need genuine Astral Crystals from the deep caverns. Mind fetching a few?",
            dialogueCompleted = "These crystals radiate raw enchantment! Bring your blade to my forge anytime and I'll upgrade it!"
        ),
        Quest(
            id = "mq_3_ancient_golem",
            title = "3. Guardian of the Ruins",
            description = "The entrance to the Sunken Citadel is guarded by the ancient Stone Golem. Defeat it to breach the citadel gates.",
            category = QuestCategory.MAIN_STORY,
            giverName = "Princess Lyra",
            targetObjective = "Defeat the Ancient Stone Golem",
            currentProgress = 0,
            targetProgress = 1,
            rewardGold = 250,
            rewardExp = 300,
            rewardItem = DefaultItems.createKnightPlate(),
            status = QuestStatus.AVAILABLE,
            dialogueIntro = "Champion, the secrets of our lost lineage lie beyond the Sunken Citadel, but an ancient titan blocks the gate. Please, bring peace to the ruins!",
            dialogueCompleted = "The titan rests at last! You have restored hope to all of Aldoria!"
        ),
        Quest(
            id = "sq_1_blacksmith_iron",
            title = "Bounty: Cave Crawlers",
            description = "The Guild posted a bounty to thin out the cavern pests threatening Oakhaven's water supply.",
            category = QuestCategory.SIDE_QUEST,
            giverName = "Guildmaster Vane",
            targetObjective = "Defeat Cave Bats or Sprout Sprites",
            currentProgress = 0,
            targetProgress = 5,
            rewardGold = 100,
            rewardExp = 120,
            rewardItem = DefaultItems.createManaPotion(3),
            status = QuestStatus.AVAILABLE,
            dialogueIntro = "Looking for extra coin? The Adventurers Guild always has open contracts. Hunt those cavern pests and I'll pay you in pure gold.",
            dialogueCompleted = "Contract satisfied! Here is your guild bounty fee."
        ),
        Quest(
            id = "sq_2_royal_crown",
            title = "Legend: The Lost Crown",
            description = "Recover the sacred Lost Crown of Aldoria from the Citadel's deepest sanctum.",
            category = QuestCategory.MAIN_STORY,
            giverName = "Princess Lyra",
            targetObjective = "Recover the Crown & conquer the Realm",
            currentProgress = 0,
            targetProgress = 1,
            rewardGold = 500,
            rewardExp = 600,
            rewardItem = DefaultItems.createLegendarySword(),
            status = QuestStatus.AVAILABLE,
            dialogueIntro = "The final trial awaits in the Citadel Sanctum. Recover the Lost Crown and unite our kingdom once more!",
            dialogueCompleted = "Long live the Champion! The Lost Kingdom has been restored to glory!"
        )
    )
}
