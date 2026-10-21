package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun BlacksmithDialog(
    hero: Hero,
    onDismiss: () -> Unit,
    onUpgradeItem: (Item) -> Unit
) {
    var selectedItemToUpgrade by remember {
        mutableStateOf<Item?>(hero.equippedWeapon ?: hero.equippedArmor)
    }

    val crystalCount = hero.inventory.firstOrNull { it.id == "m_crystal_ore" }?.quantity ?: 0
    val upgradeGoldCost = (selectedItemToUpgrade?.upgradeLevel ?: 0) * 40 + 60
    val upgradeCrystalCost = (selectedItemToUpgrade?.upgradeLevel ?: 0) + 1
    val canAfford = hero.gold >= upgradeGoldCost && crystalCount >= upgradeCrystalCost

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(540.dp)
                .border(2.dp, Color(0xFFE67E22), RoundedCornerShape(20.dp))
                .testTag("blacksmith_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Whatshot, contentDescription = "Forge", tint = Color(0xFFE67E22), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Bryan's Arcane Forge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("Hone weapons & temper armor with Astral Crystals", color = Color(0xFF9E9E9E), fontSize = 10.sp)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_blacksmith")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selectable Gear for Upgrade
                Text("SELECT GEAR TO ENHANCE", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    hero.equippedWeapon?.let { weapon ->
                        GearSelectChip(
                            item = weapon,
                            isSelected = selectedItemToUpgrade?.id == weapon.id,
                            onClick = { selectedItemToUpgrade = weapon }
                        )
                    }
                    hero.equippedArmor?.let { armor ->
                        GearSelectChip(
                            item = armor,
                            isSelected = selectedItemToUpgrade?.id == armor.id,
                            onClick = { selectedItemToUpgrade = armor }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Upgrade Preview Card
                selectedItemToUpgrade?.let { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D42)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE67E22), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Enhancement: ${item.name} (+${item.upgradeLevel} ➔ +${item.upgradeLevel + 1})",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.description, color = Color.LightGray, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stats comparison
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("CURRENT", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    val curStat = if (item.type == ItemType.WEAPON) "${item.totalAttack} Atk" else "${item.totalDefense} Def"
                                    Text(curStat, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Text("➔", color = Color(0xFFE67E22), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("ENHANCED", color = Color(0xFF2ECC71), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    val nextStat = if (item.type == ItemType.WEAPON) "${item.totalAttack + 4} Atk" else "${item.totalDefense + 3} Def"
                                    Text(nextStat, color = Color(0xFF2ECC71), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Cost Requirements
                            Text("FORGE REQUIREMENTS:", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$upgradeGoldCost Gold (Have: ${hero.gold})",
                                    color = if (hero.gold >= upgradeGoldCost) Color.White else Color(0xFFFF7675),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Icon(Icons.Default.Diamond, contentDescription = "Crystals", tint = Color(0xFF00CED1), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$upgradeCrystalCost Crystals (Have: $crystalCount)",
                                    color = if (crystalCount >= upgradeCrystalCost) Color.White else Color(0xFFFF7675),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onUpgradeItem(item) },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_confirm_upgrade")
                    ) {
                        Text(
                            text = if (canAfford) "Forge Enhancement (+${item.upgradeLevel + 1})" else "Insufficient Materials",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } ?: run {
                    Text("No weapon or armor equipped to upgrade.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun GearSelectChip(
    item: Item,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF2C3E50) else Color(0xFF0D1B2A),
        modifier = Modifier
            .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF34495E), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(item.displayName, color = item.rarity.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(item.type.name, color = Color.Gray, fontSize = 9.sp)
        }
    }
}
