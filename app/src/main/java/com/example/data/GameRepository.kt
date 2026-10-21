package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CharacterClass
import com.example.model.DefaultItems
import com.example.model.DefaultQuests
import com.example.model.Hero
import com.example.model.Item
import com.example.model.Quest
import com.example.model.QuestCategory
import com.example.model.QuestStatus
import com.example.model.ZoneType
import org.json.JSONArray
import org.json.JSONObject

data class SaveSlotMetadata(
    val slotIndex: Int,
    val heroName: String,
    val heroClass: CharacterClass,
    val level: Int,
    val currentZone: ZoneType,
    val lastSavedTimestamp: Long,
    val isEmpty: Boolean = false
)

class GameRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lost_kingdom_saves", Context.MODE_PRIVATE)

    fun hasAnySave(): Boolean {
        return prefs.contains("save_slot_0") || prefs.contains("save_slot_1") || prefs.contains("save_slot_2")
    }

    fun getSlotMetadata(slotIndex: Int): SaveSlotMetadata {
        val jsonStr = prefs.getString("save_slot_$slotIndex", null)
        if (jsonStr.isNullOrEmpty()) {
            return SaveSlotMetadata(
                slotIndex = slotIndex,
                heroName = "Empty Slot",
                heroClass = CharacterClass.WARRIOR,
                level = 1,
                currentZone = ZoneType.OAKHAVEN_VILLAGE,
                lastSavedTimestamp = 0L,
                isEmpty = true
            )
        }
        return try {
            val root = JSONObject(jsonStr)
            val heroObj = root.getJSONObject("hero")
            SaveSlotMetadata(
                slotIndex = slotIndex,
                heroName = heroObj.getString("name"),
                heroClass = CharacterClass.valueOf(heroObj.getString("class")),
                level = heroObj.getInt("level"),
                currentZone = ZoneType.valueOf(root.optString("zone", ZoneType.OAKHAVEN_VILLAGE.name)),
                lastSavedTimestamp = root.optLong("timestamp", System.currentTimeMillis()),
                isEmpty = false
            )
        } catch (_: Exception) {
            SaveSlotMetadata(slotIndex, "Corrupted Slot", CharacterClass.WARRIOR, 1, ZoneType.OAKHAVEN_VILLAGE, 0L, true)
        }
    }

    fun saveGame(slotIndex: Int, hero: Hero, zone: ZoneType, quests: List<Quest>): Boolean {
        return try {
            val root = JSONObject()
            root.put("timestamp", System.currentTimeMillis())
            root.put("zone", zone.name)

            val heroObj = JSONObject()
            heroObj.put("name", hero.name)
            heroObj.put("class", hero.heroClass.name)
            heroObj.put("level", hero.level)
            heroObj.put("currentHp", hero.currentHp)
            heroObj.put("maxHp", hero.maxHp)
            heroObj.put("currentMp", hero.currentMp)
            heroObj.put("maxMp", hero.maxMp)
            heroObj.put("exp", hero.exp)
            heroObj.put("expToNext", hero.expToNextLevel)
            heroObj.put("gold", hero.gold)
            heroObj.put("gems", hero.gems)
            heroObj.put("str", hero.statStrength)
            heroObj.put("int", hero.statIntellect)
            heroObj.put("agi", hero.statAgility)
            heroObj.put("vit", hero.statVitality)
            heroObj.put("statPoints", hero.unspentStatPoints)
            heroObj.put("skill1Level", hero.skill1Level)
            heroObj.put("skill2Level", hero.skill2Level)
            root.put("hero", heroObj)

            // Quests
            val questArray = JSONArray()
            for (q in quests) {
                val qObj = JSONObject()
                qObj.put("id", q.id)
                qObj.put("status", q.status.name)
                qObj.put("progress", q.currentProgress)
                questArray.put(qObj)
            }
            root.put("quests", questArray)

            prefs.edit().putString("save_slot_$slotIndex", root.toString()).apply()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun loadGame(slotIndex: Int): Triple<Hero, ZoneType, List<Quest>>? {
        val jsonStr = prefs.getString("save_slot_$slotIndex", null) ?: return null
        return try {
            val root = JSONObject(jsonStr)
            val zone = ZoneType.valueOf(root.optString("zone", ZoneType.OAKHAVEN_VILLAGE.name))
            val heroObj = root.getJSONObject("hero")

            val heroClass = CharacterClass.valueOf(heroObj.getString("class"))
            val hero = Hero(
                name = heroObj.getString("name"),
                heroClass = heroClass,
                level = heroObj.getInt("level"),
                currentHp = heroObj.getInt("currentHp"),
                maxHp = heroObj.getInt("maxHp"),
                currentMp = heroObj.getInt("currentMp"),
                maxMp = heroObj.getInt("maxMp"),
                exp = heroObj.getInt("exp"),
                expToNextLevel = heroObj.getInt("expToNext"),
                gold = heroObj.getInt("gold"),
                gems = heroObj.optInt("gems", 5),
                statStrength = heroObj.getInt("str"),
                statIntellect = heroObj.getInt("int"),
                statAgility = heroObj.getInt("agi"),
                statVitality = heroObj.getInt("vit"),
                unspentStatPoints = heroObj.optInt("statPoints", 0),
                skill1Level = heroObj.optInt("skill1Level", 1),
                skill2Level = heroObj.optInt("skill2Level", 1),
                equippedWeapon = when (heroClass) {
                    CharacterClass.WARRIOR -> DefaultItems.createStarterSword()
                    CharacterClass.MAGE -> DefaultItems.createCrystalStaff()
                    CharacterClass.ARCHER -> DefaultItems.createRangerBow()
                    CharacterClass.HEALER -> DefaultItems.createHolyScepter()
                },
                equippedArmor = DefaultItems.createLeatherTunic(),
                equippedOffhand = if (heroClass == CharacterClass.WARRIOR) DefaultItems.createShield() else null,
                inventory = listOf(
                    DefaultItems.createHealthPotion(4),
                    DefaultItems.createManaPotion(3),
                    DefaultItems.createCrystalOre(3)
                )
            )

            // Quests
            val quests = DefaultQuests.createQuestList().toMutableList()
            val questArray = root.optJSONArray("quests")
            if (questArray != null) {
                for (i in 0 until questArray.length()) {
                    val qObj = questArray.getJSONObject(i)
                    val id = qObj.getString("id")
                    val idx = quests.indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        val original = quests[idx]
                        quests[idx] = original.copy(
                            status = QuestStatus.valueOf(qObj.getString("status")),
                            currentProgress = qObj.getInt("progress")
                        )
                    }
                }
            }

            Triple(hero, zone, quests)
        } catch (_: Exception) {
            null
        }
    }

    fun exportSaveToJson(slotIndex: Int): String {
        return prefs.getString("save_slot_$slotIndex", "{}") ?: "{}"
    }

    fun importSaveFromJson(slotIndex: Int, json: String): Boolean {
        return try {
            val root = JSONObject(json)
            if (root.has("hero")) {
                prefs.edit().putString("save_slot_$slotIndex", json).apply()
                true
            } else false
        } catch (_: Exception) {
            false
        }
    }
}
