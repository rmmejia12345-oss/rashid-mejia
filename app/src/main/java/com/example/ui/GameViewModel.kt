package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameSoundEffects
import com.example.data.GameRepository
import com.example.data.SaveSlotMetadata
import com.example.engine.GameWorldEngine
import com.example.model.CharacterClass
import com.example.model.DefaultGuildData
import com.example.model.DefaultItems
import com.example.model.DefaultQuests
import com.example.model.Enemy
import com.example.model.GuildBounty
import com.example.model.Hero
import com.example.model.Item
import com.example.model.LeaderboardEntry
import com.example.model.MapNpc
import com.example.model.OnlineAdventurer
import com.example.model.Quest
import com.example.model.QuestStatus
import com.example.model.ZoneType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

enum class AppScreen {
    MAIN_MENU,
    CHARACTER_CREATION,
    GAMEPLAY
}

enum class ActiveDialog {
    NONE,
    INVENTORY,
    CHARACTER_STATS,
    QUEST_LOG,
    BLACKSMITH,
    ONLINE_GUILD,
    SAVE_LOAD,
    NPC_DIALOGUE,
    BUILD_GUIDE
}

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.MAIN_MENU,
    val activeDialog: ActiveDialog = ActiveDialog.NONE,
    val hero: Hero = Hero.createDefault("Sir Cedric", CharacterClass.WARRIOR),
    val quests: List<Quest> = DefaultQuests.createQuestList(),
    val partyMembers: List<OnlineAdventurer> = emptyList(),
    val allAdventurers: List<OnlineAdventurer> = DefaultGuildData.createAdventurers(),
    val bounties: List<GuildBounty> = DefaultGuildData.createBounties(),
    val leaderboard: List<LeaderboardEntry> = DefaultGuildData.createLeaderboard(),
    val activeDialogueNpc: MapNpc? = null,
    val interactionLabel: String = "",
    val isInteractingAvailable: Boolean = false,
    val isAudioMuted: Boolean = false,
    val saveSlots: List<SaveSlotMetadata> = emptyList(),
    val hasExistingSave: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val soundEffects = GameSoundEffects(application.applicationContext)
    val repository = GameRepository(application.applicationContext)
    val worldEngine = GameWorldEngine(soundEffects)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var joystickInputX = 0f
    private var joystickInputY = 0f
    private var gameLoopJob: Job? = null

    init {
        refreshSaveSlots()
    }

    fun refreshSaveSlots() {
        val slots = listOf(
            repository.getSlotMetadata(0),
            repository.getSlotMetadata(1),
            repository.getSlotMetadata(2)
        )
        _uiState.value = _uiState.value.copy(
            saveSlots = slots,
            hasExistingSave = repository.hasAnySave()
        )
    }

    fun startNewGameFlow() {
        _uiState.value = _uiState.value.copy(currentScreen = AppScreen.CHARACTER_CREATION)
    }

    fun backToMainMenu() {
        pauseGameLoop()
        _uiState.value = _uiState.value.copy(currentScreen = AppScreen.MAIN_MENU, activeDialog = ActiveDialog.NONE)
    }

    fun createHeroAndStart(name: String, heroClass: CharacterClass) {
        val hero = Hero.createDefault(name, heroClass)
        worldEngine.loadZone(ZoneType.OAKHAVEN_VILLAGE)
        _uiState.value = _uiState.value.copy(
            hero = hero,
            currentScreen = AppScreen.GAMEPLAY,
            activeDialog = ActiveDialog.NONE,
            quests = DefaultQuests.createQuestList()
        )
        startGameLoop()
        // Autosave
        repository.saveGame(0, hero, ZoneType.OAKHAVEN_VILLAGE, _uiState.value.quests)
        refreshSaveSlots()
    }

    fun continueGame() {
        val loaded = repository.loadGame(0) ?: repository.loadGame(1) ?: repository.loadGame(2)
        if (loaded != null) {
            val (hero, zone, quests) = loaded
            worldEngine.loadZone(zone)
            _uiState.value = _uiState.value.copy(
                hero = hero,
                currentScreen = AppScreen.GAMEPLAY,
                quests = quests,
                activeDialog = ActiveDialog.NONE
            )
            startGameLoop()
        } else {
            startNewGameFlow()
        }
    }

    fun openDialog(dialog: ActiveDialog) {
        _uiState.value = _uiState.value.copy(activeDialog = dialog)
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(activeDialog = ActiveDialog.NONE, activeDialogueNpc = null)
    }

    fun toggleAudio() {
        val muted = soundEffects.toggleMute()
        _uiState.value = _uiState.value.copy(isAudioMuted = muted)
    }

    fun onJoystickMove(x: Float, y: Float) {
        joystickInputX = x
        joystickInputY = y
    }

    fun onAttackClick() {
        worldEngine.performBasicAttack(_uiState.value.hero)
    }

    fun onSkill1Click() {
        val currentHero = _uiState.value.hero
        val success = worldEngine.performSkill1(currentHero) { mpCost ->
            _uiState.value = _uiState.value.copy(
                hero = currentHero.copy(currentMp = max(0, currentHero.currentMp - mpCost))
            )
        }
    }

    fun onSkill2Click() {
        val currentHero = _uiState.value.hero
        val success = worldEngine.performSkill2(currentHero) { mpCost ->
            _uiState.value = _uiState.value.copy(
                hero = currentHero.copy(currentMp = max(0, currentHero.currentMp - mpCost))
            )
        }
    }

    fun onDodgeClick() {
        worldEngine.performDodgeRoll()
    }

    fun onInteractClick() {
        val nearbyNpc = worldEngine.getNearbyNpc()
        if (nearbyNpc != null) {
            soundEffects.playLevelUpFanfare()
            _uiState.value = _uiState.value.copy(
                activeDialogueNpc = nearbyNpc,
                activeDialog = ActiveDialog.NPC_DIALOGUE
            )
            return
        }

        val nearbyTreasure = worldEngine.getNearbyTreasure()
        if (nearbyTreasure != null) {
            nearbyTreasure.isOpened = true
            soundEffects.playChestOpen()
            val cur = _uiState.value.hero
            var updatedInv: List<Item> = cur.inventory
            if (nearbyTreasure.item != null) {
                updatedInv = addItemToInventory(updatedInv, nearbyTreasure.item)
            }
            _uiState.value = _uiState.value.copy(
                hero = cur.copy(
                    gold = cur.gold + nearbyTreasure.gold,
                    inventory = updatedInv
                )
            )
            worldEngine.spawnParticles(nearbyTreasure.x, nearbyTreasure.y, androidx.compose.ui.graphics.Color.Yellow, 14)
        }
    }

    fun onQuickHealthPotion() {
        val cur = _uiState.value.hero
        val hpPotionIdx = cur.inventory.indexOfFirst { it.id == "c_hp_potion" && it.quantity > 0 }
        if (hpPotionIdx >= 0) {
            soundEffects.playPotionDrink()
            val potion = cur.inventory[hpPotionIdx]
            val newHp = (cur.currentHp + potion.healAmount).coerceAtMost(cur.maxHp)
            val updatedInv = cur.inventory.toMutableList()
            if (potion.quantity > 1) {
                updatedInv[hpPotionIdx] = potion.copy(quantity = potion.quantity - 1)
            } else {
                updatedInv.removeAt(hpPotionIdx)
            }
            _uiState.value = _uiState.value.copy(hero = cur.copy(currentHp = newHp, inventory = updatedInv))
        }
    }

    fun onQuickManaPotion() {
        val cur = _uiState.value.hero
        val mpPotionIdx = cur.inventory.indexOfFirst { it.id == "c_mp_potion" && it.quantity > 0 }
        if (mpPotionIdx >= 0) {
            soundEffects.playPotionDrink()
            val potion = cur.inventory[mpPotionIdx]
            val newMp = (cur.currentMp + potion.mpAmount).coerceAtMost(cur.maxMp)
            val updatedInv = cur.inventory.toMutableList()
            if (potion.quantity > 1) {
                updatedInv[mpPotionIdx] = potion.copy(quantity = potion.quantity - 1)
            } else {
                updatedInv.removeAt(mpPotionIdx)
            }
            _uiState.value = _uiState.value.copy(hero = cur.copy(currentMp = newMp, inventory = updatedInv))
        }
    }

    fun allocateStatPoint(stat: String) {
        val cur = _uiState.value.hero
        if (cur.unspentStatPoints <= 0) return
        soundEffects.playLevelUpFanfare()

        val updated = when (stat) {
            "STR" -> cur.copy(statStrength = cur.statStrength + 1, unspentStatPoints = cur.unspentStatPoints - 1)
            "INT" -> cur.copy(statIntellect = cur.statIntellect + 1, maxMp = cur.maxMp + 15, currentMp = cur.currentMp + 15, unspentStatPoints = cur.unspentStatPoints - 1)
            "AGI" -> cur.copy(statAgility = cur.statAgility + 1, unspentStatPoints = cur.unspentStatPoints - 1)
            "VIT" -> cur.copy(statVitality = cur.statVitality + 1, maxHp = cur.maxHp + 20, currentHp = cur.currentHp + 20, unspentStatPoints = cur.unspentStatPoints - 1)
            else -> cur
        }
        _uiState.value = _uiState.value.copy(hero = updated)
    }

    fun equipItem(item: Item) {
        val cur = _uiState.value.hero
        soundEffects.playSwordSlash()
        val updated = when (item.type) {
            com.example.model.ItemType.WEAPON -> cur.copy(equippedWeapon = item)
            com.example.model.ItemType.ARMOR -> cur.copy(equippedArmor = item)
            com.example.model.ItemType.OFFHAND -> cur.copy(equippedOffhand = item)
            else -> cur
        }
        _uiState.value = _uiState.value.copy(hero = updated)
    }

    fun useItem(item: Item) {
        if (item.type != com.example.model.ItemType.CONSUMABLE) return
        val cur = _uiState.value.hero
        soundEffects.playPotionDrink()
        val newHp = (cur.currentHp + item.healAmount).coerceAtMost(cur.maxHp)
        val newMp = (cur.currentMp + item.mpAmount).coerceAtMost(cur.maxMp)
        val updatedInv = cur.inventory.toMutableList()
        val idx = updatedInv.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            if (item.quantity > 1) {
                updatedInv[idx] = item.copy(quantity = item.quantity - 1)
            } else {
                updatedInv.removeAt(idx)
            }
        }
        _uiState.value = _uiState.value.copy(hero = cur.copy(currentHp = newHp, currentMp = newMp, inventory = updatedInv))
    }

    fun sellItem(item: Item) {
        val cur = _uiState.value.hero
        soundEffects.playCoinPickup()
        val updatedInv = cur.inventory.toMutableList()
        val idx = updatedInv.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            if (item.quantity > 1) {
                updatedInv[idx] = item.copy(quantity = item.quantity - 1)
            } else {
                updatedInv.removeAt(idx)
            }
        }
        _uiState.value = _uiState.value.copy(hero = cur.copy(gold = cur.gold + item.goldValue, inventory = updatedInv))
    }

    fun upgradeGear(item: Item) {
        val cur = _uiState.value.hero
        val crystalCount = cur.inventory.firstOrNull { it.id == "m_crystal_ore" }?.quantity ?: 0
        val goldCost = item.upgradeLevel * 40 + 60
        val crystalCost = item.upgradeLevel + 1

        if (cur.gold >= goldCost && crystalCount >= crystalCost) {
            soundEffects.playLevelUpFanfare()
            // Deduct crystals
            val updatedInv = cur.inventory.toMutableList()
            val cIdx = updatedInv.indexOfFirst { it.id == "m_crystal_ore" }
            if (cIdx >= 0) {
                val cItem = updatedInv[cIdx]
                if (cItem.quantity > crystalCost) {
                    updatedInv[cIdx] = cItem.copy(quantity = cItem.quantity - crystalCost)
                } else {
                    updatedInv.removeAt(cIdx)
                }
            }

            val upgradedItem = item.upgrade()
            val updatedHero = when (item.type) {
                com.example.model.ItemType.WEAPON -> cur.copy(equippedWeapon = upgradedItem, gold = cur.gold - goldCost, inventory = updatedInv)
                com.example.model.ItemType.ARMOR -> cur.copy(equippedArmor = upgradedItem, gold = cur.gold - goldCost, inventory = updatedInv)
                else -> cur
            }
            _uiState.value = _uiState.value.copy(hero = updatedHero)
        }
    }

    fun acceptQuest(quest: Quest) {
        soundEffects.playQuestComplete()
        val updated = _uiState.value.quests.map {
            if (it.id == quest.id) it.copy(status = QuestStatus.ACTIVE) else it
        }
        _uiState.value = _uiState.value.copy(quests = updated, activeDialog = ActiveDialog.NONE)
    }

    fun completeQuest(quest: Quest) {
        soundEffects.playQuestComplete()
        val curHero = _uiState.value.hero
        var updatedHero = curHero.copy(
            gold = curHero.gold + quest.rewardGold,
            exp = curHero.exp + quest.rewardExp
        )
        if (quest.rewardItem != null) {
            updatedHero = updatedHero.copy(
                inventory = addItemToInventory(updatedHero.inventory, quest.rewardItem)
            )
        }
        // Check level up
        updatedHero = checkLevelUp(updatedHero)

        val updatedQuests = _uiState.value.quests.map {
            if (it.id == quest.id) it.copy(status = QuestStatus.COMPLETED) else it
        }

        _uiState.value = _uiState.value.copy(
            hero = updatedHero,
            quests = updatedQuests,
            activeDialog = ActiveDialog.NONE
        )

        // Autosave on quest completion
        repository.saveGame(0, updatedHero, worldEngine.currentZone, updatedQuests)
        refreshSaveSlots()
    }

    fun togglePartyMember(adv: OnlineAdventurer) {
        soundEffects.playCoinPickup()
        val cur = _uiState.value.partyMembers
        val exists = cur.any { it.id == adv.id }
        val updated = if (exists) {
            cur.filter { it.id != adv.id }
        } else {
            if (cur.size < 3) cur + adv else cur
        }
        _uiState.value = _uiState.value.copy(partyMembers = updated)
    }

    fun claimBounty(bounty: GuildBounty) {
        soundEffects.playQuestComplete()
        val cur = _uiState.value.hero
        _uiState.value = _uiState.value.copy(
            hero = cur.copy(gold = cur.gold + bounty.goldReward, gems = cur.gems + bounty.gemReward),
            bounties = _uiState.value.bounties.map {
                if (it.id == bounty.id) it.copy(isCompleted = true) else it
            }
        )
    }

    fun saveGameToSlot(slotIndex: Int) {
        val s = _uiState.value
        soundEffects.playChestOpen()
        repository.saveGame(slotIndex, s.hero, worldEngine.currentZone, s.quests)
        refreshSaveSlots()
    }

    fun loadGameFromSlot(slotIndex: Int) {
        val loaded = repository.loadGame(slotIndex)
        if (loaded != null) {
            soundEffects.playLevelUpFanfare()
            val (hero, zone, quests) = loaded
            worldEngine.loadZone(zone)
            _uiState.value = _uiState.value.copy(
                hero = hero,
                quests = quests,
                activeDialog = ActiveDialog.NONE
            )
        }
    }

    fun exportSlot(slotIndex: Int): String {
        return repository.exportSaveToJson(slotIndex)
    }

    fun importSlot(slotIndex: Int, json: String): Boolean {
        val ok = repository.importSaveFromJson(slotIndex, json)
        if (ok) refreshSaveSlots()
        return ok
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            while (isActive) {
                val now = System.nanoTime()
                val deltaSec = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = now

                // Check proximity for interaction label
                val nearbyNpc = worldEngine.getNearbyNpc()
                val nearbyTreasure = worldEngine.getNearbyTreasure()
                val isAvailable = nearbyNpc != null || nearbyTreasure != null
                val label = when {
                    nearbyNpc != null -> "Talk to ${nearbyNpc.name}"
                    nearbyTreasure != null -> "Open Treasure Chest"
                    else -> ""
                }
                if (_uiState.value.isInteractingAvailable != isAvailable || _uiState.value.interactionLabel != label) {
                    _uiState.value = _uiState.value.copy(
                        isInteractingAvailable = isAvailable,
                        interactionLabel = label
                    )
                }

                // Tick game physics
                worldEngine.update(
                    deltaSec = deltaSec,
                    joystickX = joystickInputX,
                    joystickY = joystickInputY,
                    hero = _uiState.value.hero,
                    onHeroDamaged = { dmg ->
                        val cur = _uiState.value.hero
                        val newHp = max(0, cur.currentHp - dmg)
                        _uiState.value = _uiState.value.copy(hero = cur.copy(currentHp = newHp))
                    },
                    onEnemyKilled = { enemy ->
                        onEnemyDefeated(enemy)
                    },
                    onZoneChange = { newZone ->
                        // Autosave on zone change
                        repository.saveGame(0, _uiState.value.hero, newZone, _uiState.value.quests)
                        refreshSaveSlots()
                    }
                )

                delay(16) // ~60 FPS
            }
        }
    }

    private fun onEnemyDefeated(enemy: Enemy) {
        soundEffects.playCoinPickup()
        val cur = _uiState.value.hero
        var updatedHero = cur.copy(
            gold = cur.gold + enemy.type.goldReward,
            exp = cur.exp + enemy.type.expReward
        )
        // Chance to drop crystals
        if (kotlin.random.Random.nextFloat() < 0.45f) {
            updatedHero = updatedHero.copy(
                inventory = addItemToInventory(updatedHero.inventory, DefaultItems.createCrystalOre(1))
            )
        }
        updatedHero = checkLevelUp(updatedHero)

        // Progress active quests
        val updatedQuests = _uiState.value.quests.map { quest ->
            if (quest.status == QuestStatus.ACTIVE && quest.currentProgress < quest.targetProgress) {
                quest.copy(currentProgress = quest.currentProgress + 1)
            } else quest
        }

        _uiState.value = _uiState.value.copy(
            hero = updatedHero,
            quests = updatedQuests
        )
    }

    private fun checkLevelUp(hero: Hero): Hero {
        var h = hero
        while (h.exp >= h.expToNextLevel) {
            soundEffects.playLevelUpFanfare()
            val newLevel = h.level + 1
            val leftoverExp = h.exp - h.expToNextLevel
            val nextReq = (h.expToNextLevel * 1.45f).toInt()
            h = h.copy(
                level = newLevel,
                exp = leftoverExp,
                expToNextLevel = nextReq,
                maxHp = h.maxHp + 25,
                currentHp = h.maxHp + 25,
                maxMp = h.maxMp + 15,
                currentMp = h.maxMp + 15,
                unspentStatPoints = h.unspentStatPoints + 3,
                gems = h.gems + 2
            )
        }
        return h
    }

    private fun addItemToInventory(inv: List<Item>, item: Item): List<Item> {
        val list = inv.toMutableList()
        val existingIdx = list.indexOfFirst { it.id == item.id }
        if (existingIdx >= 0 && item.type == com.example.model.ItemType.CONSUMABLE || item.type == com.example.model.ItemType.MATERIAL) {
            val existing = list[existingIdx]
            list[existingIdx] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            list.add(item)
        }
        return list
    }

    private fun pauseGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    override fun onCleared() {
        super.onCleared()
        pauseGameLoop()
    }
}
