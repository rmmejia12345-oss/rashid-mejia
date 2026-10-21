package com.example.engine

import androidx.compose.ui.graphics.Color
import com.example.audio.GameSoundEffects
import com.example.model.CharacterClass
import com.example.model.DamageNumber
import com.example.model.DefaultItems
import com.example.model.Enemy
import com.example.model.EnemyState
import com.example.model.EnemyType
import com.example.model.Hero
import com.example.model.Item
import com.example.model.MapNpc
import com.example.model.MapPortal
import com.example.model.MapProp
import com.example.model.MapTreasure
import com.example.model.Projectile
import com.example.model.PropType
import com.example.model.Quest
import com.example.model.QuestStatus
import com.example.model.SlashArc
import com.example.model.VisualParticle
import com.example.model.ZoneType
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class GameWorldEngine(
    private val soundEffects: GameSoundEffects
) {
    // Player world state
    var playerX: Float = 400f
    var playerY: Float = 400f
    var playerFacingAngle: Float = 0f // In radians
    var isMoving: Boolean = false
    var walkAnimTimer: Float = 0f

    // Combat state
    var attackCooldownTimer: Float = 0f
    var skill1CooldownTimer: Float = 0f
    var skill2CooldownTimer: Float = 0f
    var isDodgeRolling: Boolean = false
    var dodgeRollTimer: Float = 0f
    var dodgeRollVx: Float = 0f
    var dodgeRollVy: Float = 0f
    var isShieldGuarding: Boolean = false
    var shieldGuardTimer: Float = 0f

    // Zone & Entities
    var currentZone: ZoneType = ZoneType.OAKHAVEN_VILLAGE
        private set
    val enemies = mutableListOf<Enemy>()
    val projectiles = mutableListOf<Projectile>()
    val particles = mutableListOf<VisualParticle>()
    val damageNumbers = mutableListOf<DamageNumber>()
    val slashArcs = mutableListOf<SlashArc>()

    // Map features
    val npcs = mutableListOf<MapNpc>()
    val portals = mutableListOf<MapPortal>()
    val treasures = mutableListOf<MapTreasure>()
    val props = mutableListOf<MapProp>()

    // World bounds
    val worldWidth = 1400f
    val worldHeight = 1400f

    private var nextEntityId = 1L

    init {
        loadZone(ZoneType.OAKHAVEN_VILLAGE)
    }

    fun loadZone(zone: ZoneType, spawnAtPortal: Boolean = false) {
        currentZone = zone
        enemies.clear()
        projectiles.clear()
        particles.clear()
        damageNumbers.clear()
        slashArcs.clear()
        npcs.clear()
        portals.clear()
        treasures.clear()
        props.clear()

        when (zone) {
            ZoneType.OAKHAVEN_VILLAGE -> setupOakhaven()
            ZoneType.WHISPERING_WOODS -> setupWhisperingWoods()
            ZoneType.CRYSTAL_CAVERNS -> setupCrystalCaverns()
            ZoneType.SUNKEN_CITADEL -> setupSunkenCitadel()
        }

        if (!spawnAtPortal) {
            playerX = 700f
            playerY = 700f
        }
    }

    private fun setupOakhaven() {
        // Village NPCs
        npcs.add(MapNpc("npc_rowan", "Elder Rowan", "Village Elder", 620f, 620f, Color(0xFFD4AC0D), Color(0xFF2C3E50), "mq_1_slime_troubles"))
        npcs.add(MapNpc("npc_bryan", "Blacksmith Bryan", "Master Forger", 850f, 640f, Color(0xFFE67E22), Color(0xFF7F8C8D), "mq_2_crystal_core"))
        npcs.add(MapNpc("npc_lyra", "Princess Lyra", "Royal Emissary", 700f, 520f, Color(0xFFF39C12), Color(0xFF8E44AD), "mq_3_ancient_golem"))
        npcs.add(MapNpc("npc_vane", "Guildmaster Vane", "Adventurers Guild", 550f, 780f, Color(0xFF34495E), Color(0xFFC0392B), "sq_1_blacksmith_iron"))

        // Portals
        portals.add(MapPortal("p_to_forest", ZoneType.WHISPERING_WOODS, 700f, 1300f, 60f, "To Whispering Woods"))

        // Props (Village buildings, fountain, flower patches)
        props.add(MapProp(PropType.FOUNTAIN, 700f, 700f, 50f))
        props.add(MapProp(PropType.HOUSE, 500f, 480f, 70f))
        props.add(MapProp(PropType.HOUSE, 900f, 480f, 70f))
        props.add(MapProp(PropType.HOUSE, 420f, 780f, 65f))
        props.add(MapProp(PropType.CAMPFIRE, 700f, 850f, 35f))
        props.add(MapProp(PropType.TREE, 280f, 320f, 45f))
        props.add(MapProp(PropType.TREE, 1120f, 320f, 45f))
        props.add(MapProp(PropType.TREE, 300f, 1100f, 45f))
        props.add(MapProp(PropType.TREE, 1100f, 1100f, 45f))
        props.add(MapProp(PropType.FLOWER_BED, 600f, 690f, 25f))
        props.add(MapProp(PropType.FLOWER_BED, 800f, 690f, 25f))

        // Treasures
        treasures.add(MapTreasure("t_village_1", 340f, 460f, gold = 50, item = DefaultItems.createHealthPotion(2)))
    }

    private fun setupWhisperingWoods() {
        // Portals
        portals.add(MapPortal("p_to_village", ZoneType.OAKHAVEN_VILLAGE, 700f, 120f, 60f, "Return to Oakhaven"))
        portals.add(MapPortal("p_to_caverns", ZoneType.CRYSTAL_CAVERNS, 700f, 1300f, 60f, "Deep Crystal Caverns"))

        // Enemies
        enemies.add(Enemy("slime_1", EnemyType.FOREST_SLIME, 500f, 450f))
        enemies.add(Enemy("slime_2", EnemyType.FOREST_SLIME, 900f, 500f))
        enemies.add(Enemy("slime_3", EnemyType.FOREST_SLIME, 450f, 850f))
        enemies.add(Enemy("slime_4", EnemyType.FOREST_SLIME, 880f, 900f))
        enemies.add(Enemy("sprite_1", EnemyType.SPROUT_SPRITE, 600f, 1050f))
        enemies.add(Enemy("sprite_2", EnemyType.SPROUT_SPRITE, 780f, 1080f))
        enemies.add(Enemy("boss_slime", EnemyType.BOSS_SLIME_KING, 700f, 1150f))

        // Props (Lush trees, glowing mushrooms)
        for (i in 0 until 18) {
            val angle = i * (2 * PI / 18)
            val dist = 520f + (i % 3) * 60f
            props.add(MapProp(PropType.TREE, 700f + cos(angle).toFloat() * dist, 700f + sin(angle).toFloat() * dist, 48f))
        }

        // Treasures
        treasures.add(MapTreasure("t_woods_1", 380f, 750f, gold = 80, item = DefaultItems.createCrystalOre(3)))
        treasures.add(MapTreasure("t_woods_2", 1050f, 850f, gold = 100, item = DefaultItems.createManaPotion(3)))
    }

    private fun setupCrystalCaverns() {
        // Portals
        portals.add(MapPortal("p_to_woods", ZoneType.WHISPERING_WOODS, 700f, 120f, 60f, "To Whispering Woods"))
        portals.add(MapPortal("p_to_citadel", ZoneType.SUNKEN_CITADEL, 700f, 1300f, 60f, "To Sunken Citadel"))

        // Enemies
        enemies.add(Enemy("bat_1", EnemyType.CAVE_BAT, 480f, 400f))
        enemies.add(Enemy("bat_2", EnemyType.CAVE_BAT, 920f, 420f))
        enemies.add(Enemy("bat_3", EnemyType.CAVE_BAT, 650f, 750f))
        enemies.add(Enemy("golem_1", EnemyType.CRYSTAL_GOLEM, 520f, 920f))
        enemies.add(Enemy("golem_2", EnemyType.CRYSTAL_GOLEM, 880f, 950f))

        // Props (Glowing crystal stalagmites)
        props.add(MapProp(PropType.CRYSTAL_CLUSTER, 500f, 600f, 40f))
        props.add(MapProp(PropType.CRYSTAL_CLUSTER, 900f, 620f, 42f))
        props.add(MapProp(PropType.CRYSTAL_CLUSTER, 400f, 900f, 45f))
        props.add(MapProp(PropType.CRYSTAL_CLUSTER, 1000f, 920f, 45f))
        props.add(MapProp(PropType.CRYSTAL_CLUSTER, 700f, 800f, 50f))

        treasures.add(MapTreasure("t_cavern_1", 320f, 980f, gold = 150, item = DefaultItems.createCrystalOre(5)))
    }

    private fun setupSunkenCitadel() {
        // Portals
        portals.add(MapPortal("p_to_caverns_back", ZoneType.CRYSTAL_CAVERNS, 700f, 120f, 60f, "Back to Caverns"))

        // Boss & Sentinels
        enemies.add(Enemy("boss_ancient", EnemyType.BOSS_ANCIENT_GOLEM, 700f, 750f))
        enemies.add(Enemy("citadel_golem_1", EnemyType.CRYSTAL_GOLEM, 520f, 600f))
        enemies.add(Enemy("citadel_golem_2", EnemyType.CRYSTAL_GOLEM, 880f, 600f))

        // Ancient ruins props
        props.add(MapProp(PropType.STATUE, 700f, 350f, 55f))
        props.add(MapProp(PropType.RUIN_PILLAR, 450f, 500f, 45f))
        props.add(MapProp(PropType.RUIN_PILLAR, 950f, 500f, 45f))
        props.add(MapProp(PropType.RUIN_PILLAR, 450f, 900f, 45f))
        props.add(MapProp(PropType.RUIN_PILLAR, 950f, 900f, 45f))

        treasures.add(MapTreasure("t_citadel_royal", 700f, 1050f, gold = 400, item = DefaultItems.createLegendarySword()))
    }

    fun update(
        deltaSec: Float,
        joystickX: Float,
        joystickY: Float,
        hero: Hero,
        onHeroDamaged: (Int) -> Unit,
        onEnemyKilled: (Enemy) -> Unit,
        onZoneChange: (ZoneType) -> Unit
    ) {
        val dt = deltaSec.coerceIn(0.001f, 0.05f)

        // 1. Timers & Cooldowns
        if (attackCooldownTimer > 0f) attackCooldownTimer = max(0f, attackCooldownTimer - dt)
        if (skill1CooldownTimer > 0f) skill1CooldownTimer = max(0f, skill1CooldownTimer - dt)
        if (skill2CooldownTimer > 0f) skill2CooldownTimer = max(0f, skill2CooldownTimer - dt)

        if (shieldGuardTimer > 0f) {
            shieldGuardTimer -= dt
            if (shieldGuardTimer <= 0f) isShieldGuarding = false
        }

        // 2. Dodge Roll
        if (isDodgeRolling) {
            dodgeRollTimer -= dt
            playerX = (playerX + dodgeRollVx * dt).coerceIn(80f, worldWidth - 80f)
            playerY = (playerY + dodgeRollVy * dt).coerceIn(80f, worldHeight - 80f)
            if (dodgeRollTimer <= 0f) {
                isDodgeRolling = false
            }
        } else {
            // 3. Movement
            val len = hypot(joystickX, joystickY)
            if (len > 0.15f) {
                isMoving = true
                walkAnimTimer += dt * 8f
                val normX = joystickX / len
                val normY = joystickY / len
                playerFacingAngle = atan2(normY, normX)

                val baseSpeed = 220f * hero.moveSpeedMultiplier
                val targetSpeed = baseSpeed * len.coerceIn(0f, 1f)

                val newX = (playerX + normX * targetSpeed * dt).coerceIn(80f, worldWidth - 80f)
                val newY = (playerY + normY * targetSpeed * dt).coerceIn(80f, worldHeight - 80f)

                // Simple collision check with props
                var canMove = true
                for (p in props) {
                    if (p.type == PropType.HOUSE || p.type == PropType.FOUNTAIN || p.type == PropType.STATUE) {
                        if (hypot(newX - p.x, newY - p.y) < p.size + 15f) {
                            canMove = false
                            break
                        }
                    }
                }
                if (canMove) {
                    playerX = newX
                    playerY = newY
                }
            } else {
                isMoving = false
            }
        }

        // 4. Check Portals
        for (portal in portals) {
            val dist = hypot(playerX - portal.x, playerY - portal.y)
            if (dist < portal.radius) {
                soundEffects.playChestOpen()
                loadZone(portal.targetZone, spawnAtPortal = false)
                onZoneChange(portal.targetZone)
                return
            }
        }

        // 5. Update Enemies
        val itEnemy = enemies.iterator()
        while (itEnemy.hasNext()) {
            val enemy = itEnemy.next()
            if (!enemy.isAlive) {
                itEnemy.remove()
                onEnemyKilled(enemy)
                continue
            }

            if (enemy.hitFlashTimer > 0f) enemy.hitFlashTimer -= dt
            val distToPlayer = hypot(playerX - enemy.x, playerY - enemy.y)

            // Telegraph & Attack update
            if (enemy.isTelegraphing) {
                enemy.telegraphProgress += dt / 0.8f // 0.8s telegraph warning
                if (enemy.telegraphProgress >= 1f) {
                    // Attack lands!
                    enemy.isTelegraphing = false
                    enemy.telegraphProgress = 0f
                    enemy.attackTimer = enemy.type.attackCooldownSec

                    // Check if player still within attack range and not dodge rolling
                    val finalDist = hypot(playerX - enemy.x, playerY - enemy.y)
                    if (finalDist <= enemy.type.attackRange + 25f && !isDodgeRolling) {
                        var rawDamage = enemy.type.baseAttack
                        val mitigation = hero.totalDefense * 0.4f
                        var netDamage = max(3, (rawDamage - mitigation).toInt())
                        if (isShieldGuarding) {
                            netDamage = max(1, (netDamage * 0.35f).toInt())
                        }
                        onHeroDamaged(netDamage)
                        soundEffects.playHitImpact()
                        damageNumbers.add(
                            DamageNumber(
                                id = nextEntityId++,
                                value = netDamage,
                                x = playerX,
                                y = playerY - 30f,
                                isBlocked = isShieldGuarding
                            )
                        )
                        spawnParticles(playerX, playerY, Color.Red, 8)
                    }
                }
            } else {
                if (enemy.attackTimer > 0f) enemy.attackTimer -= dt

                // Aggro distance
                val aggroRange = if (enemy.type.isBoss) 500f else 320f
                if (distToPlayer < aggroRange) {
                    enemy.state = EnemyState.CHASING
                    val angle = atan2(playerY - enemy.y, playerX - enemy.x)

                    // If in attack range and cooldown ready -> start telegraph!
                    if (distToPlayer <= enemy.type.attackRange && enemy.attackTimer <= 0f) {
                        enemy.isTelegraphing = true
                        enemy.telegraphProgress = 0f
                        enemy.state = EnemyState.TELEGRAPHING
                    } else if (distToPlayer > enemy.type.attackRange * 0.8f) {
                        // Chase player
                        val speed = enemy.type.moveSpeed * 100f * dt
                        enemy.x += cos(angle) * speed
                        enemy.y += sin(angle) * speed
                    }
                } else {
                    enemy.state = EnemyState.IDLE
                }
            }
        }

        // 6. Update Projectiles
        val itProj = projectiles.iterator()
        while (itProj.hasNext()) {
            val p = itProj.next()
            val dx = p.targetX - p.x
            val dy = p.targetY - p.y
            val angle = atan2(dy, dx)
            val step = p.speed * dt
            p.x += cos(angle) * step
            p.y += sin(angle) * step
            p.distanceTraveled += step

            // Hit test
            var hit = false
            if (p.isPlayerOwned) {
                for (enemy in enemies) {
                    if (enemy.isAlive && hypot(enemy.x - p.x, enemy.y - p.y) < 32f) {
                        hit = true
                        applyDamageToEnemy(enemy, p.damage, isCrit = Random.nextFloat() < hero.critChance)
                        spawnParticles(p.x, p.y, p.color, 10)
                        soundEffects.playHitImpact()
                        if (!p.isPiercing) break
                    }
                }
            } else {
                // Enemy projectile hitting player
                if (!isDodgeRolling && hypot(playerX - p.x, playerY - p.y) < 28f) {
                    hit = true
                    val netDamage = max(2, p.damage - (hero.totalDefense * 0.3f).toInt())
                    onHeroDamaged(netDamage)
                    soundEffects.playHitImpact()
                    damageNumbers.add(DamageNumber(nextEntityId++, netDamage, playerX, playerY - 30f))
                    spawnParticles(playerX, playerY, Color.Red, 8)
                }
            }

            if ((hit && !p.isPiercing) || p.distanceTraveled >= p.maxDistance) {
                itProj.remove()
            }
        }

        // 7. Update Slash Arcs
        val itArc = slashArcs.iterator()
        while (itArc.hasNext()) {
            val arc = itArc.next()
            arc.progress += dt / arc.duration
            if (arc.progress >= 1f) {
                itArc.remove()
            }
        }

        // 8. Update Damage Numbers
        val itDam = damageNumbers.iterator()
        while (itDam.hasNext()) {
            val dn = itDam.next()
            dn.y -= dt * 45f
            dn.lifetime -= dt
            dn.alpha = (dn.lifetime / 0.8f).coerceIn(0f, 1f)
            if (dn.lifetime <= 0f) itDam.remove()
        }

        // 9. Update Particles
        val itPart = particles.iterator()
        while (itPart.hasNext()) {
            val pt = itPart.next()
            pt.x += pt.vx * dt
            pt.y += pt.vy * dt
            pt.life -= dt
            pt.alpha = (pt.life / pt.maxLife).coerceIn(0f, 1f)
            if (pt.life <= 0f) itPart.remove()
        }
    }

    fun performBasicAttack(hero: Hero) {
        if (attackCooldownTimer > 0f) return
        attackCooldownTimer = 0.45f

        val baseAtk = hero.totalAttack
        val isCrit = Random.nextFloat() < hero.critChance
        val damage = if (isCrit) (baseAtk * 1.8f).toInt() else baseAtk

        when (hero.heroClass) {
            CharacterClass.WARRIOR -> {
                soundEffects.playSwordSlash()
                slashArcs.add(
                    SlashArc(
                        id = nextEntityId++,
                        x = playerX + cos(playerFacingAngle) * 35f,
                        y = playerY + sin(playerFacingAngle) * 35f,
                        angle = playerFacingAngle,
                        radius = 65f,
                        color = Color(0xFFFFD700)
                    )
                )
                // Hit enemies in front arc
                for (enemy in enemies) {
                    if (enemy.isAlive) {
                        val dist = hypot(enemy.x - playerX, enemy.y - playerY)
                        val angleToEnemy = atan2(enemy.y - playerY, enemy.x - playerX)
                        val angleDiff = Math.abs(angleDiff(playerFacingAngle, angleToEnemy))
                        if (dist < 85f && angleDiff < PI / 2.2) {
                            applyDamageToEnemy(enemy, damage, isCrit)
                            spawnParticles(enemy.x, enemy.y, Color.Yellow, 8)
                        }
                    }
                }
            }
            CharacterClass.MAGE -> {
                soundEffects.playMagicCast()
                val targetX = playerX + cos(playerFacingAngle) * 400f
                val targetY = playerY + sin(playerFacingAngle) * 400f
                projectiles.add(
                    Projectile(
                        id = nextEntityId++,
                        x = playerX,
                        y = playerY,
                        targetX = targetX,
                        targetY = targetY,
                        speed = 460f,
                        damage = damage,
                        isPlayerOwned = true,
                        color = Color(0xFF00D2D3),
                        radius = 10f
                    )
                )
            }
            CharacterClass.ARCHER -> {
                soundEffects.playSwordSlash()
                val targetX = playerX + cos(playerFacingAngle) * 450f
                val targetY = playerY + sin(playerFacingAngle) * 450f
                projectiles.add(
                    Projectile(
                        id = nextEntityId++,
                        x = playerX,
                        y = playerY,
                        targetX = targetX,
                        targetY = targetY,
                        speed = 580f,
                        damage = damage,
                        isPlayerOwned = true,
                        color = Color(0xFF2ECC71),
                        radius = 7f,
                        isPiercing = false
                    )
                )
            }
            CharacterClass.HEALER -> {
                soundEffects.playMagicCast()
                // Radiant Ray piercing straight forward
                val targetX = playerX + cos(playerFacingAngle) * 380f
                val targetY = playerY + sin(playerFacingAngle) * 380f
                projectiles.add(
                    Projectile(
                        id = nextEntityId++,
                        x = playerX,
                        y = playerY,
                        targetX = targetX,
                        targetY = targetY,
                        speed = 500f,
                        damage = damage,
                        isPlayerOwned = true,
                        color = Color(0xFFF1C40F),
                        radius = 12f,
                        isPiercing = true
                    )
                )
            }
        }
    }

    fun performSkill1(hero: Hero, onMpDeducted: (Int) -> Unit): Boolean {
        if (skill1CooldownTimer > 0f) return false
        if (hero.currentMp < hero.heroClass.skill1MpCost) return false

        skill1CooldownTimer = hero.heroClass.skill1CooldownSec
        onMpDeducted(hero.heroClass.skill1MpCost)

        val baseAtk = hero.totalAttack
        when (hero.heroClass) {
            CharacterClass.WARRIOR -> {
                // Whirlwind Slash: 360 spin hitting all enemies within 110 radius
                soundEffects.playSwordSlash()
                slashArcs.add(SlashArc(nextEntityId++, playerX, playerY, 0f, 110f, Color(0xFFE74C3C), duration = 0.35f))
                for (enemy in enemies) {
                    if (enemy.isAlive && hypot(enemy.x - playerX, enemy.y - playerY) < 115f) {
                        applyDamageToEnemy(enemy, (baseAtk * 1.9f).toInt(), isCrit = true)
                        spawnParticles(enemy.x, enemy.y, Color(0xFFE74C3C), 12)
                    }
                }
            }
            CharacterClass.MAGE -> {
                // Arcane Missiles: 3 homing bolts
                soundEffects.playMagicCast()
                for (i in -1..1) {
                    val angleOffset = i * 0.25f
                    val angle = playerFacingAngle + angleOffset
                    projectiles.add(
                        Projectile(
                            id = nextEntityId++,
                            x = playerX,
                            y = playerY,
                            targetX = playerX + cos(angle) * 420f,
                            targetY = playerY + sin(angle) * 420f,
                            speed = 480f,
                            damage = (baseAtk * 0.9f).toInt(),
                            isPlayerOwned = true,
                            color = Color(0xFF9B59B6),
                            radius = 9f
                        )
                    )
                }
            }
            CharacterClass.ARCHER -> {
                // Piercing Snipe: High velocity bolt that cuts through all foes
                soundEffects.playSwordSlash()
                val targetX = playerX + cos(playerFacingAngle) * 500f
                val targetY = playerY + sin(playerFacingAngle) * 500f
                projectiles.add(
                    Projectile(
                        id = nextEntityId++,
                        x = playerX,
                        y = playerY,
                        targetX = targetX,
                        targetY = targetY,
                        speed = 700f,
                        damage = (baseAtk * 2.2f).toInt(),
                        isPlayerOwned = true,
                        color = Color(0xFFF39C12),
                        radius = 10f,
                        isPiercing = true
                    )
                )
            }
            CharacterClass.HEALER -> {
                // Divine Blessing: Heals 35% of max HP
                soundEffects.playPotionDrink()
                val healAmt = (hero.maxHp * 0.35f).toInt()
                damageNumbers.add(DamageNumber(nextEntityId++, healAmt, playerX, playerY - 35f, isHeal = true))
                spawnParticles(playerX, playerY, Color(0xFF2ECC71), 16)
            }
        }
        return true
    }

    fun performSkill2(hero: Hero, onMpDeducted: (Int) -> Unit): Boolean {
        if (skill2CooldownTimer > 0f) return false
        if (hero.currentMp < hero.heroClass.skill2MpCost) return false

        skill2CooldownTimer = hero.heroClass.skill2CooldownSec
        onMpDeducted(hero.heroClass.skill2MpCost)

        val baseAtk = hero.totalAttack
        when (hero.heroClass) {
            CharacterClass.WARRIOR -> {
                // Iron Bulwark: 65% damage reduction for 5 sec
                soundEffects.playChestOpen()
                isShieldGuarding = true
                shieldGuardTimer = 5.0f
                spawnParticles(playerX, playerY, Color(0xFFFFD700), 20)
            }
            CharacterClass.MAGE -> {
                // Frost Nova: AoE ground blast that deals high damage to surrounding area
                soundEffects.playMagicCast()
                slashArcs.add(SlashArc(nextEntityId++, playerX, playerY, 0f, 140f, Color(0xFF3498DB), duration = 0.45f))
                for (enemy in enemies) {
                    if (enemy.isAlive && hypot(enemy.x - playerX, enemy.y - playerY) < 145f) {
                        applyDamageToEnemy(enemy, (baseAtk * 1.7f).toInt(), isCrit = true)
                        spawnParticles(enemy.x, enemy.y, Color(0xFF3498DB), 14)
                    }
                }
            }
            CharacterClass.ARCHER -> {
                // Arrow Volley: cluster of arrows
                soundEffects.playSwordSlash()
                for (i in -2..2) {
                    val angle = playerFacingAngle + (i * 0.18f)
                    projectiles.add(
                        Projectile(
                            id = nextEntityId++,
                            x = playerX,
                            y = playerY,
                            targetX = playerX + cos(angle) * 400f,
                            targetY = playerY + sin(angle) * 400f,
                            speed = 520f,
                            damage = (baseAtk * 0.85f).toInt(),
                            isPlayerOwned = true,
                            color = Color(0xFF27AE60),
                            radius = 7f
                        )
                    )
                }
            }
            CharacterClass.HEALER -> {
                // Sunburst Blast: huge radiant beam
                soundEffects.playLevelUpFanfare()
                for (enemy in enemies) {
                    if (enemy.isAlive && hypot(enemy.x - playerX, enemy.y - playerY) < 160f) {
                        applyDamageToEnemy(enemy, (baseAtk * 2.0f).toInt(), isCrit = true)
                        spawnParticles(enemy.x, enemy.y, Color(0xFFF1C40F), 18)
                    }
                }
            }
        }
        return true
    }

    fun performDodgeRoll(): Boolean {
        if (isDodgeRolling) return false
        isDodgeRolling = true
        dodgeRollTimer = 0.28f
        val rollSpeed = 520f
        dodgeRollVx = cos(playerFacingAngle) * rollSpeed
        dodgeRollVy = sin(playerFacingAngle) * rollSpeed
        soundEffects.playDodgeRoll()
        spawnParticles(playerX, playerY, Color.White, 8)
        return true
    }

    private fun applyDamageToEnemy(enemy: Enemy, rawDamage: Int, isCrit: Boolean) {
        val netDamage = max(1, rawDamage - enemy.type.baseDefense)
        enemy.currentHp = max(0, enemy.currentHp - netDamage)
        enemy.hitFlashTimer = 0.12f
        enemy.state = EnemyState.HURT

        damageNumbers.add(
            DamageNumber(
                id = nextEntityId++,
                value = netDamage,
                x = enemy.x,
                y = enemy.y - 25f,
                isCritical = isCrit
            )
        )
    }

    fun spawnParticles(x: Float, y: Float, color: Color, count: Int) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val speed = Random.nextFloat() * 120f + 40f
            particles.add(
                VisualParticle(
                    x = x,
                    y = y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    size = Random.nextFloat() * 4f + 3f,
                    life = 0.4f,
                    maxLife = 0.4f
                )
            )
        }
    }

    fun getNearbyNpc(): MapNpc? {
        return npcs.firstOrNull { hypot(playerX - it.x, playerY - it.y) < 70f }
    }

    fun getNearbyTreasure(): MapTreasure? {
        return treasures.firstOrNull { !it.isOpened && hypot(playerX - it.x, playerY - it.y) < 65f }
    }

    private fun angleDiff(a: Float, b: Float): Float {
        var diff = b - a
        while (diff < -PI) diff += (2 * PI).toFloat()
        while (diff > PI) diff -= (2 * PI).toFloat()
        return diff
    }
}
