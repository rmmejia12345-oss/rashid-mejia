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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.data.SaveSlotMetadata
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SaveLoadDialog(
    slots: List<SaveSlotMetadata>,
    onDismiss: () -> Unit,
    onSaveToSlot: (Int) -> Unit,
    onLoadFromSlot: (Int) -> Unit,
    onExportSlot: (Int) -> String,
    onImportSlot: (Int, String) -> Boolean
) {
    var exportString by remember { mutableStateOf("") }
    var importString by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
                .border(2.dp, Color(0xFF415A77), RoundedCornerShape(20.dp))
                .testTag("save_load_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Save & Load Game Progress", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("Local device persistence & cloud backup", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_save_load")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (statusMessage.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(statusMessage, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Save Slots
                Text("ADVENTURE SAVE SLOTS", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                for (slot in slots) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D42)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, if (!slot.isEmpty) Color(0xFF415A77) else Color(0xFF2E3D52), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Slot ${slot.slotIndex + 1}", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (!slot.isEmpty) {
                                    Text(
                                        text = dateFormat.format(Date(slot.lastSavedTimestamp)),
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (slot.isEmpty) {
                                Text("Empty Save Slot", color = Color.Gray, fontSize = 11.sp)
                            } else {
                                Text(
                                    text = "${slot.heroName} (Lv.${slot.level} ${slot.heroClass.title})",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text("Location: ${slot.currentZone.title}", color = Color(0xFF81C784), fontSize = 10.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        onSaveToSlot(slot.slotIndex)
                                        statusMessage = "Successfully saved game to Slot ${slot.slotIndex + 1}!"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                                    modifier = Modifier.height(32.dp).testTag("btn_save_slot_${slot.slotIndex}")
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save Here", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                if (!slot.isEmpty) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            onLoadFromSlot(slot.slotIndex)
                                            statusMessage = "Loaded game from Slot ${slot.slotIndex + 1}!"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                                        modifier = Modifier.height(32.dp).testTag("btn_load_slot_${slot.slotIndex}")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = "Load", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Load", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            exportString = onExportSlot(slot.slotIndex)
                                            statusMessage = "Export JSON generated for Slot ${slot.slotIndex + 1}!"
                                        },
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Export", color = Color(0xFFFFD700), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Backup JSON Import/Export
                Text("CLOUD / JSON BACKUP EXPORT", color = Color(0xFF00CED1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                if (exportString.isNotEmpty()) {
                    OutlinedTextField(
                        value = exportString,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Exported Save Payload", color = Color.Gray, fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0D1B2A),
                            unfocusedContainerColor = Color(0xFF0D1B2A)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                OutlinedTextField(
                    value = importString,
                    onValueChange = { importString = it },
                    placeholder = { Text("Paste save JSON here to restore progress...", color = Color.Gray, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth().height(70.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D1B2A),
                        unfocusedContainerColor = Color(0xFF0D1B2A)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        if (importString.isNotBlank()) {
                            val success = onImportSlot(0, importString)
                            statusMessage = if (success) "Save successfully restored into Slot 1!" else "Invalid save JSON format."
                            if (success) importString = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B59B6)),
                    modifier = Modifier.align(Alignment.End).height(34.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = "Import", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restore into Slot 1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
