package com.example.model

import androidx.compose.ui.graphics.Color

enum class ZoneType(
    val title: String,
    val subtitle: String,
    val ambientColor: Color,
    val groundColor: Color,
    val accentColor: Color,
    val isPeaceful: Boolean
) {
    OAKHAVEN_VILLAGE(
        title = "Oakhaven Village",
        subtitle = "Peaceful Haven of Aldoria",
        ambientColor = Color(0xFF6B8E23),
        groundColor = Color(0xFF5D7A28),
        accentColor = Color(0xFFE5B25D),
        isPeaceful = true
    ),
    WHISPERING_WOODS(
        title = "Whispering Woods",
        subtitle = "Forest of the Emerald Slimes",
        ambientColor = Color(0xFF2E6F40),
        groundColor = Color(0xFF235432),
        accentColor = Color(0xFF38A169),
        isPeaceful = false
    ),
    CRYSTAL_CAVERNS(
        title = "Crystal Caverns",
        subtitle = "Deep Luminous Grotto",
        ambientColor = Color(0xFF2A3459),
        groundColor = Color(0xFF1E243D),
        accentColor = Color(0xFF00D2D3),
        isPeaceful = false
    ),
    SUNKEN_CITADEL(
        title = "Sunken Citadel",
        subtitle = "Ancient Royal Sanctum",
        ambientColor = Color(0xFF4A3E5C),
        groundColor = Color(0xFF372D45),
        accentColor = Color(0xFFFFD700),
        isPeaceful = false
    )
}

data class MapNpc(
    val id: String,
    val name: String,
    val title: String,
    val x: Float,
    val y: Float,
    val hairColor: Color,
    val robeColor: Color,
    val associatedQuestId: String? = null
)

data class MapPortal(
    val id: String,
    val targetZone: ZoneType,
    val x: Float,
    val y: Float,
    val radius: Float = 55f,
    val label: String
)

data class MapTreasure(
    val id: String,
    val x: Float,
    val y: Float,
    var isOpened: Boolean = false,
    val gold: Int = 40,
    val item: Item? = null
)

data class MapProp(
    val type: PropType,
    val x: Float,
    val y: Float,
    val size: Float = 36f
)

enum class PropType {
    HOUSE,
    CASTLE_TOWER,
    TREE,
    FLOWER_BED,
    FOUNTAIN,
    CRYSTAL_CLUSTER,
    CAMPFIRE,
    STATUE,
    RUIN_PILLAR
}
