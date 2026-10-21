package com.example.model

data class Hero(
    val name: String = "Sir Cedric",
    val heroClass: CharacterClass = CharacterClass.WARRIOR,
    val level: Int = 1,
    val currentHp: Int = 220,
    val maxHp: Int = 220,
    val currentMp: Int = 60,
    val maxMp: Int = 60,
    val exp: Int = 0,
    val expToNextLevel: Int = 100,
    val gold: Int = 150,
    val gems: Int = 5,
    val statStrength: Int = 16,
    val statIntellect: Int = 6,
    val statAgility: Int = 10,
    val statVitality: Int = 16,
    val unspentStatPoints: Int = 0,
    val equippedWeapon: Item? = DefaultItems.createStarterSword(),
    val equippedArmor: Item? = DefaultItems.createLeatherTunic(),
    val equippedOffhand: Item? = DefaultItems.createShield(),
    val inventory: List<Item> = listOf(
        DefaultItems.createHealthPotion(3),
        DefaultItems.createManaPotion(2),
        DefaultItems.createCrystalOre(3)
    ),
    val skill1Level: Int = 1,
    val skill2Level: Int = 1
) {
    // Derived combat stats
    val totalAttack: Int
        get() {
            val classBonus = when (heroClass) {
                CharacterClass.WARRIOR -> (statStrength * 2.2f).toInt()
                CharacterClass.MAGE -> (statIntellect * 2.4f).toInt()
                CharacterClass.ARCHER -> (statAgility * 2.3f).toInt()
                CharacterClass.HEALER -> ((statIntellect * 1.5f) + (statStrength * 0.8f)).toInt()
            }
            val weaponAtk = equippedWeapon?.totalAttack ?: 0
            return classBonus + weaponAtk
        }

    val totalDefense: Int
        get() {
            val vitBonus = (statVitality * 1.4f).toInt()
            val armorDef = equippedArmor?.totalDefense ?: 0
            val offhandDef = equippedOffhand?.totalDefense ?: 0
            return vitBonus + armorDef + offhandDef
        }

    val critChance: Float
        get() = (0.05f + (statAgility * 0.0075f)).coerceIn(0.05f, 0.60f)

    val moveSpeedMultiplier: Float
        get() = (1.0f + (statAgility * 0.006f)).coerceIn(1.0f, 1.45f)

    companion object {
        fun createDefault(name: String, heroClass: CharacterClass): Hero {
            val starterWeapon = when (heroClass) {
                CharacterClass.WARRIOR -> DefaultItems.createStarterSword()
                CharacterClass.MAGE -> DefaultItems.createCrystalStaff()
                CharacterClass.ARCHER -> DefaultItems.createRangerBow()
                CharacterClass.HEALER -> DefaultItems.createHolyScepter()
            }
            return Hero(
                name = name,
                heroClass = heroClass,
                level = 1,
                currentHp = heroClass.baseHp,
                maxHp = heroClass.baseHp,
                currentMp = heroClass.baseMp,
                maxMp = heroClass.baseMp,
                exp = 0,
                expToNextLevel = 100,
                gold = 120,
                gems = 5,
                statStrength = heroClass.baseStr,
                statIntellect = heroClass.baseInt,
                statAgility = heroClass.baseAgi,
                statVitality = heroClass.baseVit,
                unspentStatPoints = 0,
                equippedWeapon = starterWeapon,
                equippedArmor = DefaultItems.createLeatherTunic(),
                equippedOffhand = if (heroClass == CharacterClass.WARRIOR) DefaultItems.createShield() else null,
                inventory = listOf(
                    DefaultItems.createHealthPotion(3),
                    DefaultItems.createManaPotion(2),
                    DefaultItems.createCrystalOre(2)
                )
            )
        }
    }
}
