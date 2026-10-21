package com.example.model

import androidx.compose.ui.graphics.Color

enum class ItemRarity(val label: String, val color: Color) {
    COMMON("Common", Color(0xFFBDC3C7)),
    RARE("Rare", Color(0xFF3498DB)),
    EPIC("Epic", Color(0xFF9B59B6)),
    LEGENDARY("Legendary", Color(0xFFF39C12))
}

enum class ItemType {
    WEAPON,
    ARMOR,
    OFFHAND,
    ACCESSORY,
    CONSUMABLE,
    MATERIAL
}

data class Item(
    val id: String,
    val name: String,
    val description: String,
    val type: ItemType,
    val rarity: ItemRarity,
    val iconName: String,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val hpBonus: Int = 0,
    val mpBonus: Int = 0,
    val healAmount: Int = 0,
    val mpAmount: Int = 0,
    val upgradeLevel: Int = 0,
    val goldValue: Int = 20,
    val quantity: Int = 1
) {
    val displayName: String
        get() = if (upgradeLevel > 0) "$name +$upgradeLevel" else name

    val totalAttack: Int
        get() = attackBonus + (upgradeLevel * 4)

    val totalDefense: Int
        get() = defenseBonus + (upgradeLevel * 3)

    fun upgrade(): Item {
        return copy(upgradeLevel = upgradeLevel + 1)
    }
}

object DefaultItems {
    fun createStarterSword(): Item = Item(
        id = "w_iron_sword",
        name = "Iron Knight Blade",
        description = "A reliable tempered steel broadsword carried by Aldorian guards.",
        type = ItemType.WEAPON,
        rarity = ItemRarity.COMMON,
        iconName = "sword",
        attackBonus = 12,
        goldValue = 40
    )

    fun createCrystalStaff(): Item = Item(
        id = "w_crystal_staff",
        name = "Glimmering Wand",
        description = "Carved from blue quartz found in the Crystal Caverns.",
        type = ItemType.WEAPON,
        rarity = ItemRarity.RARE,
        iconName = "staff",
        attackBonus = 18,
        mpBonus = 30,
        goldValue = 120
    )

    fun createRangerBow(): Item = Item(
        id = "w_ranger_bow",
        name = "Whispering Yew Bow",
        description = "Fires arrows with swift quiet speed.",
        type = ItemType.WEAPON,
        rarity = ItemRarity.RARE,
        iconName = "bow",
        attackBonus = 16,
        goldValue = 110
    )

    fun createHolyScepter(): Item = Item(
        id = "w_holy_scepter",
        name = "Dawnlight Scepter",
        description = "Inscribed with holy runes of sunlight.",
        type = ItemType.WEAPON,
        rarity = ItemRarity.RARE,
        iconName = "scepter",
        attackBonus = 14,
        hpBonus = 40,
        goldValue = 115
    )

    fun createLeatherTunic(): Item = Item(
        id = "a_leather_tunic",
        name = "Reinforced Tunic",
        description = "Sturdy boiled leather providing lightweight defense.",
        type = ItemType.ARMOR,
        rarity = ItemRarity.COMMON,
        iconName = "armor",
        defenseBonus = 8,
        hpBonus = 25,
        goldValue = 50
    )

    fun createKnightPlate(): Item = Item(
        id = "a_knight_plate",
        name = "Royal Plate Mail",
        description = "Polished steel breastplate forged in Oakhaven.",
        type = ItemType.ARMOR,
        rarity = ItemRarity.EPIC,
        iconName = "plate",
        defenseBonus = 22,
        hpBonus = 75,
        goldValue = 250
    )

    fun createShield(): Item = Item(
        id = "o_shield",
        name = "Heraldic Buckler",
        description = "Sturdy wooden round shield adorned with the Aldorian crest.",
        type = ItemType.OFFHAND,
        rarity = ItemRarity.COMMON,
        iconName = "shield",
        defenseBonus = 6,
        goldValue = 35
    )

    fun createHealthPotion(qty: Int = 3): Item = Item(
        id = "c_hp_potion",
        name = "Healing Elixir",
        description = "Sweet berry potion that restores 80 Health immediately.",
        type = ItemType.CONSUMABLE,
        rarity = ItemRarity.COMMON,
        iconName = "potion_hp",
        healAmount = 80,
        goldValue = 15,
        quantity = qty
    )

    fun createManaPotion(qty: Int = 2): Item = Item(
        id = "c_mp_potion",
        name = "Starlight Draught",
        description = "Glowing azure vial that replenishes 60 Mana instantly.",
        type = ItemType.CONSUMABLE,
        rarity = ItemRarity.COMMON,
        iconName = "potion_mp",
        mpAmount = 60,
        goldValue = 15,
        quantity = qty
    )

    fun createCrystalOre(qty: Int = 1): Item = Item(
        id = "m_crystal_ore",
        name = "Astral Crystal Shard",
        description = "A glowing mineral used by Blacksmith Bryan to enhance weapons and armor.",
        type = ItemType.MATERIAL,
        rarity = ItemRarity.RARE,
        iconName = "crystal",
        goldValue = 25,
        quantity = qty
    )

    fun createLegendarySword(): Item = Item(
        id = "w_excalibur",
        name = "Blade of Aldoria",
        description = "The ancient royal sword recovered from the Sunken Citadel.",
        type = ItemType.WEAPON,
        rarity = ItemRarity.LEGENDARY,
        iconName = "sword_legendary",
        attackBonus = 35,
        hpBonus = 50,
        goldValue = 600
    )
}
