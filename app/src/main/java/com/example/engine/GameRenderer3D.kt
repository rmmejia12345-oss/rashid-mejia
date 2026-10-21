package com.example.engine

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.model.CharacterClass
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.Hero
import com.example.model.MapNpc
import com.example.model.MapPortal
import com.example.model.MapProp
import com.example.model.MapTreasure
import com.example.model.Projectile
import com.example.model.PropType
import com.example.model.SlashArc
import com.example.model.VisualParticle
import com.example.model.ZoneType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object GameRenderer3D {

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 28f
        isFakeBoldText = true
    }

    private val namePaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 24f
        isFakeBoldText = true
        setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
    }

    fun renderWorld(
        drawScope: DrawScope,
        engine: GameWorldEngine,
        hero: Hero,
        screenWidth: Float,
        screenHeight: Float
    ) {
        with(drawScope) {
            val camX = engine.playerX - screenWidth / 2f
            val camY = engine.playerY - screenHeight / 2f

            // Helper to transform world coords to screen coords
            fun toScreenX(wx: Float) = wx - camX
            fun toScreenY(wy: Float) = wy - camY

            // 1. Draw World Background & 2.5D Isometric Ground Tiles
            drawRect(
                color = engine.currentZone.ambientColor,
                size = Size(screenWidth, screenHeight)
            )

            // Ground grid pattern (textured tiles)
            val tileSize = 90f
            val startTileX = ((camX / tileSize).toInt() - 1) * tileSize
            val endTileX = startTileX + screenWidth + tileSize * 2
            val startTileY = ((camY / tileSize).toInt() - 1) * tileSize
            val endTileY = startTileY + screenHeight + tileSize * 2

            var curY = startTileY
            while (curY < endTileY) {
                var curX = startTileX
                while (curX < endTileX) {
                    val sx = toScreenX(curX)
                    val sy = toScreenY(curY)
                    val tileHash = ((curX.toInt() * 73856093) xor (curY.toInt() * 19349663)) and 0x7FFFFFFF
                    val shade = (tileHash % 15) / 255f

                    val tileBase = engine.currentZone.groundColor
                    val tileColor = Color(
                        red = (tileBase.red + shade).coerceIn(0f, 1f),
                        green = (tileBase.green + shade).coerceIn(0f, 1f),
                        blue = (tileBase.blue + shade).coerceIn(0f, 1f)
                    )

                    drawRect(
                        color = tileColor,
                        topLeft = Offset(sx, sy),
                        size = Size(tileSize - 2f, tileSize - 2f)
                    )

                    // Subtle inner shadow / border for 3D depth
                    drawRect(
                        color = Color(0x22000000),
                        topLeft = Offset(sx, sy),
                        size = Size(tileSize - 2f, tileSize - 2f),
                        style = Stroke(width = 1.5f)
                    )

                    curX += tileSize
                }
                curY += tileSize
            }

            // 2. Portals
            for (portal in engine.portals) {
                val sx = toScreenX(portal.x)
                val sy = toScreenY(portal.y)
                // Glowing outer ring
                drawCircle(
                    color = Color(0x5500D2D3),
                    radius = portal.radius + 10f,
                    center = Offset(sx, sy)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00D2D3), Color(0xFF6C5CE7), Color.Transparent),
                        center = Offset(sx, sy),
                        radius = portal.radius
                    ),
                    radius = portal.radius,
                    center = Offset(sx, sy)
                )
                drawCircle(
                    color = Color.White,
                    radius = portal.radius * 0.4f,
                    center = Offset(sx, sy)
                )
                // Portal Label
                drawContext.canvas.nativeCanvas.drawText(
                    portal.label,
                    sx,
                    sy - portal.radius - 8f,
                    namePaint.apply { color = android.graphics.Color.WHITE }
                )
            }

            // 3. Props (Houses, Trees, Fountains, Campfires, Crystals)
            for (prop in engine.props) {
                val sx = toScreenX(prop.x)
                val sy = toScreenY(prop.y)

                when (prop.type) {
                    PropType.HOUSE -> {
                        // Drop shadow
                        drawOval(
                            color = Color(0x44000000),
                            topLeft = Offset(sx - prop.size - 10f, sy + prop.size * 0.4f),
                            size = Size(prop.size * 2.2f, prop.size * 0.8f)
                        )
                        // Walls
                        drawRect(
                            color = Color(0xFFD7CCC8),
                            topLeft = Offset(sx - prop.size, sy - prop.size * 0.4f),
                            size = Size(prop.size * 2f, prop.size * 1.2f)
                        )
                        // Roof (Slanted 3D)
                        val roofPath = Path().apply {
                            moveTo(sx, sy - prop.size * 1.4f)
                            lineTo(sx - prop.size * 1.25f, sy - prop.size * 0.35f)
                            lineTo(sx + prop.size * 1.25f, sy - prop.size * 0.35f)
                            close()
                        }
                        drawPath(roofPath, color = Color(0xFFC0392B))
                        // Wooden Door & Window
                        drawRect(
                            color = Color(0xFF5D4037),
                            topLeft = Offset(sx - 12f, sy + prop.size * 0.2f),
                            size = Size(24f, prop.size * 0.6f)
                        )
                        drawCircle(color = Color(0xFFFFD54F), radius = 10f, center = Offset(sx - 35f, sy))
                        drawCircle(color = Color(0xFFFFD54F), radius = 10f, center = Offset(sx + 35f, sy))
                    }
                    PropType.TREE -> {
                        // Drop shadow
                        drawOval(
                            color = Color(0x44000000),
                            topLeft = Offset(sx - prop.size * 0.7f, sy + prop.size * 0.5f),
                            size = Size(prop.size * 1.4f, prop.size * 0.5f)
                        )
                        // Trunk
                        drawRect(
                            color = Color(0xFF6D4C41),
                            topLeft = Offset(sx - 8f, sy),
                            size = Size(16f, prop.size * 0.8f)
                        )
                        // Layered Foliage (3D depth)
                        drawCircle(color = Color(0xFF1E824C), radius = prop.size * 0.9f, center = Offset(sx, sy - 15f))
                        drawCircle(color = Color(0xFF27AE60), radius = prop.size * 0.75f, center = Offset(sx, sy - 35f))
                        drawCircle(color = Color(0xFF2ECC71), radius = prop.size * 0.55f, center = Offset(sx, sy - 52f))
                    }
                    PropType.FOUNTAIN -> {
                        drawOval(
                            color = Color(0x44000000),
                            topLeft = Offset(sx - prop.size - 5f, sy + 15f),
                            size = Size(prop.size * 2.1f, prop.size * 0.7f)
                        )
                        drawCircle(color = Color(0xFF95A5A6), radius = prop.size, center = Offset(sx, sy))
                        drawCircle(color = Color(0xFF3498DB), radius = prop.size * 0.8f, center = Offset(sx, sy))
                        drawCircle(color = Color(0xFFE0F7FA), radius = prop.size * 0.35f, center = Offset(sx, sy))
                    }
                    PropType.CAMPFIRE -> {
                        drawOval(color = Color(0x44000000), topLeft = Offset(sx - 20f, sy + 5f), size = Size(40f, 15f))
                        drawCircle(color = Color(0xFF795548), radius = 22f, center = Offset(sx, sy))
                        drawCircle(color = Color(0xFFE67E22), radius = 16f, center = Offset(sx, sy - 6f))
                        drawCircle(color = Color(0xFFF1C40F), radius = 9f, center = Offset(sx, sy - 10f))
                    }
                    PropType.CRYSTAL_CLUSTER -> {
                        // Glowing crystal spears
                        drawCircle(color = Color(0x3300CED1), radius = prop.size * 1.2f, center = Offset(sx, sy))
                        val p1 = Path().apply {
                            moveTo(sx, sy - prop.size * 1.3f)
                            lineTo(sx - 16f, sy + 10f)
                            lineTo(sx + 16f, sy + 10f)
                            close()
                        }
                        drawPath(p1, color = Color(0xFF00CED1))
                        val p2 = Path().apply {
                            moveTo(sx - 18f, sy - prop.size * 0.9f)
                            lineTo(sx - 30f, sy + 10f)
                            lineTo(sx - 6f, sy + 10f)
                            close()
                        }
                        drawPath(p2, color = Color(0xFF8E44AD))
                    }
                    PropType.RUIN_PILLAR, PropType.STATUE, PropType.CASTLE_TOWER -> {
                        drawOval(color = Color(0x55000000), topLeft = Offset(sx - prop.size * 0.8f, sy + 10f), size = Size(prop.size * 1.6f, 25f))
                        drawRect(color = Color(0xFFBDC3C7), topLeft = Offset(sx - prop.size * 0.6f, sy - prop.size * 1.4f), size = Size(prop.size * 1.2f, prop.size * 1.8f))
                        drawRect(color = Color(0xFF7F8C8D), topLeft = Offset(sx - prop.size * 0.8f, sy - prop.size * 1.6f), size = Size(prop.size * 1.6f, prop.size * 0.3f))
                    }
                    PropType.FLOWER_BED -> {
                        drawCircle(color = Color(0xFF27AE60), radius = prop.size, center = Offset(sx, sy))
                        drawCircle(color = Color(0xFFE91E63), radius = 6f, center = Offset(sx - 8f, sy - 5f))
                        drawCircle(color = Color(0xFFFFEB3B), radius = 6f, center = Offset(sx + 8f, sy - 2f))
                        drawCircle(color = Color(0xFF9C27B0), radius = 6f, center = Offset(sx, sy + 8f))
                    }
                }
            }

            // 4. Treasures
            for (t in engine.treasures) {
                val sx = toScreenX(t.x)
                val sy = toScreenY(t.y)
                drawOval(color = Color(0x44000000), topLeft = Offset(sx - 20f, sy + 8f), size = Size(40f, 16f))
                if (t.isOpened) {
                    drawRect(color = Color(0xFF8D6E63), topLeft = Offset(sx - 18f, sy - 10f), size = Size(36f, 22f))
                } else {
                    drawRect(color = Color(0xFFD35400), topLeft = Offset(sx - 18f, sy - 14f), size = Size(36f, 26f))
                    drawRect(color = Color(0xFFFFD700), topLeft = Offset(sx - 5f, sy - 14f), size = Size(10f, 26f))
                    // Sparkle
                    drawCircle(color = Color.Yellow, radius = 4f, center = Offset(sx, sy - 20f))
                }
            }

            // 5. NPCs
            for (npc in engine.npcs) {
                val sx = toScreenX(npc.x)
                val sy = toScreenY(npc.y)
                // Shadow
                drawOval(color = Color(0x44000000), topLeft = Offset(sx - 18f, sy + 14f), size = Size(36f, 14f))
                // Robe / Body
                drawCircle(color = npc.robeColor, radius = 18f, center = Offset(sx, sy + 4f))
                // Head
                drawCircle(color = Color(0xFFFFCC80), radius = 14f, center = Offset(sx, sy - 16f))
                // Hair / Hat
                drawCircle(color = npc.hairColor, radius = 14f, center = Offset(sx, sy - 22f))

                // Nameplate
                drawContext.canvas.nativeCanvas.drawText(
                    npc.name,
                    sx,
                    sy - 42f,
                    namePaint.apply { color = android.graphics.Color.WHITE }
                )
                // Subtitle
                drawContext.canvas.nativeCanvas.drawText(
                    "[${npc.title}]",
                    sx,
                    sy - 26f,
                    namePaint.apply {
                        color = android.graphics.Color.YELLOW
                        textSize = 20f
                    }
                )
                // Quest indicator exclamation mark
                drawCircle(color = Color(0xFFFFD700), radius = 10f, center = Offset(sx, sy - 60f))
                drawContext.canvas.nativeCanvas.drawText(
                    "!",
                    sx,
                    sy - 52f,
                    textPaint.apply {
                        color = android.graphics.Color.BLACK
                        textSize = 22f
                    }
                )
            }

            // 6. Enemies & Telegraphs
            for (enemy in engine.enemies) {
                val sx = toScreenX(enemy.x)
                val sy = toScreenY(enemy.y)

                // Telegraph Warning Circle
                if (enemy.isTelegraphing) {
                    val radius = enemy.type.attackRange + 15f
                    // Outer danger boundary
                    drawCircle(
                        color = Color(0x55E74C3C),
                        radius = radius,
                        center = Offset(sx, sy)
                    )
                    drawCircle(
                        color = Color(0xFFE74C3C),
                        radius = radius,
                        center = Offset(sx, sy),
                        style = Stroke(width = 3f)
                    )
                    // Expanding inner fill
                    drawCircle(
                        color = Color(0x66E74C3C),
                        radius = radius * enemy.telegraphProgress,
                        center = Offset(sx, sy)
                    )
                }

                // Enemy Body
                val flashColor = if (enemy.hitFlashTimer > 0f) Color.White else null
                val bodyColor = flashColor ?: enemy.type.primaryColor

                drawOval(
                    color = Color(0x44000000),
                    topLeft = Offset(sx - 20f, sy + 14f),
                    size = Size(40f, 15f)
                )

                if (enemy.type.isBoss) {
                    // Massive boss body
                    val bossRadius = 38f
                    drawCircle(color = bodyColor, radius = bossRadius, center = Offset(sx, sy))
                    drawCircle(color = enemy.type.secondaryColor, radius = bossRadius * 0.7f, center = Offset(sx, sy - 6f))
                    // Golden Boss Crown
                    drawRect(color = Color(0xFFFFD700), topLeft = Offset(sx - 22f, sy - bossRadius - 16f), size = Size(44f, 18f))
                    // Eyes
                    drawCircle(color = Color.White, radius = 7f, center = Offset(sx - 12f, sy - 8f))
                    drawCircle(color = Color.White, radius = 7f, center = Offset(sx + 12f, sy - 8f))
                    drawCircle(color = Color.Black, radius = 4f, center = Offset(sx - 11f, sy - 8f))
                    drawCircle(color = Color.Black, radius = 4f, center = Offset(sx + 13f, sy - 8f))
                } else {
                    val r = if (enemy.type == EnemyType.CRYSTAL_GOLEM) 26f else 20f
                    drawCircle(color = bodyColor, radius = r, center = Offset(sx, sy))
                    // Cute Eyes
                    drawCircle(color = Color.White, radius = 4f, center = Offset(sx - 7f, sy - 5f))
                    drawCircle(color = Color.White, radius = 4f, center = Offset(sx + 7f, sy - 5f))
                    drawCircle(color = Color.Black, radius = 2.5f, center = Offset(sx - 6f, sy - 5f))
                    drawCircle(color = Color.Black, radius = 2.5f, center = Offset(sx + 8f, sy - 5f))
                }

                // Enemy HP Bar
                val barWidth = if (enemy.type.isBoss) 120f else 60f
                val barHeight = 8f
                val hpPct = (enemy.currentHp.toFloat() / enemy.maxHp).coerceIn(0f, 1f)
                val barX = sx - barWidth / 2f
                val barY = sy - (if (enemy.type.isBoss) 65f else 38f)

                drawRect(color = Color(0x99000000), topLeft = Offset(barX, barY), size = Size(barWidth, barHeight))
                drawRect(
                    color = if (enemy.type.isBoss) Color(0xFFFF9F43) else Color(0xFFE74C3C),
                    topLeft = Offset(barX, barY),
                    size = Size(barWidth * hpPct, barHeight)
                )

                // Enemy Name
                drawContext.canvas.nativeCanvas.drawText(
                    enemy.type.displayName,
                    sx,
                    barY - 6f,
                    namePaint.apply {
                        color = if (enemy.type.isBoss) android.graphics.Color.YELLOW else android.graphics.Color.WHITE
                        textSize = if (enemy.type.isBoss) 24f else 18f
                    }
                )
            }

            // 7. Hero Player Model
            val psx = toScreenX(engine.playerX)
            val psy = toScreenY(engine.playerY)

            // Player Shadow
            drawOval(
                color = Color(0x55000000),
                topLeft = Offset(psx - 22f, psy + 16f),
                size = Size(44f, 18f)
            )

            // Shield Bulwark Aura (if active)
            if (engine.isShieldGuarding) {
                drawCircle(
                    color = Color(0x44FFD700),
                    radius = 42f,
                    center = Offset(psx, psy)
                )
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = 42f,
                    center = Offset(psx, psy),
                    style = Stroke(width = 3f)
                )
            }

            // Walk animation bob
            val bob = if (engine.isMoving) sin(engine.walkAnimTimer) * 4f else 0f

            // Character Cape
            val capeColor = hero.heroClass.accentColor
            val facingDeg = (engine.playerFacingAngle * 180f / PI).toFloat()
            rotate(degrees = facingDeg, pivot = Offset(psx, psy)) {
                // Cape behind
                drawRect(
                    color = capeColor,
                    topLeft = Offset(psx - 14f, psy - 12f + bob),
                    size = Size(28f, 26f)
                )
                // Hero Body / Armor
                drawCircle(
                    color = hero.heroClass.primaryColor,
                    radius = 18f,
                    center = Offset(psx, psy + bob)
                )
                // Head
                drawCircle(
                    color = Color(0xFFFFCC80),
                    radius = 14f,
                    center = Offset(psx, psy - 16f + bob)
                )
                // Helmet / Hair
                drawCircle(
                    color = hero.heroClass.accentColor,
                    radius = 14f,
                    center = Offset(psx, psy - 22f + bob)
                )

                // Weapon representation
                drawRect(
                    color = Color(0xFFBDC3C7),
                    topLeft = Offset(psx + 16f, psy - 26f + bob),
                    size = Size(8f, 28f)
                )
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = 5f,
                    center = Offset(psx + 20f, psy + 2f + bob)
                )

                // Offhand Shield (if warrior)
                if (hero.heroClass == CharacterClass.WARRIOR) {
                    drawOval(
                        color = Color(0xFF34495E),
                        topLeft = Offset(psx - 26f, psy - 10f + bob),
                        size = Size(10f, 22f)
                    )
                }
            }

            // Player Name & Level Badge
            drawContext.canvas.nativeCanvas.drawText(
                "${hero.name} (Lv.${hero.level})",
                psx,
                psy - 48f,
                namePaint.apply {
                    color = android.graphics.Color.WHITE
                    textSize = 22f
                }
            )

            // 8. Projectiles
            for (p in engine.projectiles) {
                val prx = toScreenX(p.x)
                val pry = toScreenY(p.y)
                drawCircle(color = p.color, radius = p.radius, center = Offset(prx, pry))
                drawCircle(color = Color.White, radius = p.radius * 0.45f, center = Offset(prx, pry))
            }

            // 9. Slash Arcs
            for (arc in engine.slashArcs) {
                val asx = toScreenX(arc.x)
                val asy = toScreenY(arc.y)
                drawCircle(
                    color = arc.color.copy(alpha = (1f - arc.progress).coerceIn(0f, 1f)),
                    radius = arc.radius * arc.progress,
                    center = Offset(asx, asy),
                    style = Stroke(width = 6f)
                )
            }

            // 10. Particles
            for (pt in engine.particles) {
                val ptx = toScreenX(pt.x)
                val pty = toScreenY(pt.y)
                drawCircle(
                    color = pt.color.copy(alpha = pt.alpha),
                    radius = pt.size,
                    center = Offset(ptx, pty)
                )
            }

            // 11. Floating Damage Numbers
            for (dn in engine.damageNumbers) {
                val dsx = toScreenX(dn.x)
                val dsy = toScreenY(dn.y)
                val paintColor = when {
                    dn.isHeal -> android.graphics.Color.GREEN
                    dn.isBlocked -> android.graphics.Color.CYAN
                    dn.isCritical -> android.graphics.Color.YELLOW
                    else -> android.graphics.Color.WHITE
                }
                textPaint.color = paintColor
                textPaint.alpha = (dn.alpha * 255).toInt().coerceIn(0, 255)
                val text = when {
                    dn.isHeal -> "+${dn.value} HP"
                    dn.isBlocked -> "BLOCKED (${dn.value})"
                    dn.isCritical -> "CRIT! ${dn.value}"
                    else -> "${dn.value}"
                }
                drawContext.canvas.nativeCanvas.drawText(text, dsx, dsy, textPaint)
            }
        }
    }
}
