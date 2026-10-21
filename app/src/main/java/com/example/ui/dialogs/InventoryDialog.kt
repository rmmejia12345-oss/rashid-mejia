package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Hero
import com.example.model.Item
import com.example.model.ItemType

@Composable
fun InventoryDialog(
    hero: Hero,
    onDismiss: () -> Unit,
    onEquipItem: (Item) -> Unit,
    onUseItem: (Item) -> Unit,
    onSellItem: (Item) -> Unit
) {
    var selectedItem by remember {
        mutableStateOf<Item?>(hero.equippedWeapon ?: hero.inventory.firstOrNull())
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
                .border(2.dp, Color(0xFF415A77), RoundedCornerShape(20.dp))
                .testTag("inventory_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Adventurer's Inventory",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Manage equipment & battle items",
                            color = Color(0xFF9E9E9E),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_inventory")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Equipped Gear Row
                Text(
                    text = "EQUIPPED GEAR",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EquipSlotCard(
                        title = "Weapon",
                        item = hero.equippedWeapon,
                        isSelected = selectedItem == hero.equippedWeapon,
                        icon = Icons.Default.SportsKabaddi,
                        onClick = { selectedItem = hero.equippedWeapon }
                    )
                    EquipSlotCard(
                        title = "Armor",
                        item = hero.equippedArmor,
                        isSelected = selectedItem == hero.equippedArmor,
                        icon = Icons.Default.Shield,
                        onClick = { selectedItem = hero.equippedArmor }
                    )
                    EquipSlotCard(
                        title = "Offhand",
                        item = hero.equippedOffhand,
                        isSelected = selectedItem == hero.equippedOffhand,
                        icon = Icons.Default.FitnessCenter,
                        onClick = { selectedItem = hero.equippedOffhand }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Backpack Grid
                Text(
                    text = "BACKPACK (${hero.inventory.size}/24)",
                    color = Color(0xFF81C784),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFF0D1B2A), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF2E3D52), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    if (hero.inventory.isEmpty()) {
                        Text(
                            text = "Backpack is empty! Explore the world to find loot.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(hero.inventory) { item ->
                                val isSelected = selectedItem?.id == item.id
                                InventoryItemCell(
                                    item = item,
                                    isSelected = isSelected,
                                    onClick = { selectedItem = item }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Item Details Panel
                selectedItem?.let { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D42)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, item.rarity.color, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.displayName,
                                        color = item.rarity.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${item.rarity.label} ${item.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${item.goldValue} Gold",
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = item.description,
                                color = Color(0xFFB0BEC5),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Item Stat Bonuses
                            Row(modifier = Modifier.fillMaxWidth()) {
                                if (item.attackBonus > 0) {
                                    Text("⚔ +${item.totalAttack} Atk  ", color = Color(0xFFFF7675), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (item.defenseBonus > 0) {
                                    Text("🛡 +${item.totalDefense} Def  ", color = Color(0xFF74B9FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (item.healAmount > 0) {
                                    Text("💚 Restores ${item.healAmount} HP  ", color = Color(0xFF55EFC4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (item.mpAmount > 0) {
                                    Text("💧 Restores ${item.mpAmount} MP  ", color = Color(0xFF81ECEC), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (item.type == ItemType.CONSUMABLE) {
                                    Button(
                                        onClick = { onUseItem(item) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                                        modifier = Modifier.height(34.dp).testTag("btn_use_item")
                                    ) {
                                        Text("Drink / Use", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                } else if (item.type == ItemType.WEAPON || item.type == ItemType.ARMOR || item.type == ItemType.OFFHAND) {
                                    val isEquipped = hero.equippedWeapon == item || hero.equippedArmor == item || hero.equippedOffhand == item
                                    if (!isEquipped) {
                                        Button(
                                            onClick = { onEquipItem(item) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                                            modifier = Modifier.height(34.dp).testTag("btn_equip_item")
                                        ) {
                                            Text("Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }

                                OutlinedButton(
                                    onClick = { onSellItem(item) },
                                    modifier = Modifier.height(34.dp).testTag("btn_sell_item")
                                ) {
                                    Text("Sell (${item.goldValue}g)", color = Color(0xFFFFD700), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipSlotCard(
    title: String,
    item: Item?,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF2C3E50) else Color(0xFF0D1B2A),
        modifier = Modifier
            .width(102.dp)
            .height(64.dp)
            .border(
                1.5.dp,
                if (isSelected) Color(0xFFFFD700) else Color(0xFF34495E),
                RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(title, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = item?.displayName ?: "None",
                color = item?.rarity?.color ?: Color.DarkGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            if (item != null) {
                val stat = if (item.totalAttack > 0) "+${item.totalAttack} ATK" else "+${item.totalDefense} DEF"
                Text(stat, color = Color.White, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun InventoryItemCell(
    item: Item,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF2C3E50) else Color(0xFF1B263B),
        modifier = Modifier
            .size(64.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFFFD700) else item.rarity.color,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val symbol = when (item.type) {
                    ItemType.WEAPON -> "⚔"
                    ItemType.ARMOR -> "🛡"
                    ItemType.OFFHAND -> "🛡"
                    ItemType.CONSUMABLE -> "🧪"
                    ItemType.MATERIAL -> "💎"
                    else -> "📦"
                }
                Text(symbol, fontSize = 20.sp)
                Text(
                    text = item.displayName,
                    color = item.rarity.color,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            if (item.quantity > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color(0xDD000000), RoundedCornerShape(topStart = 6.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "x${item.quantity}",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
