package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Technician
import com.example.data.Zone
import com.example.ui.theme.appTextFieldColors
import com.example.ui.theme.IHCAmber
import com.example.ui.theme.IHCAmberLight
import com.example.ui.theme.IHCNavyBlue
import com.example.ui.theme.IHCPrimaryGreen
import com.example.ui.theme.IHCPrimaryGreenLight
import com.example.ui.theme.IHCRed
import com.example.ui.theme.IHCRedLight
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ZoneManagementBottomSheet(
    viewModel: TechnicianViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val zones by viewModel.allZones.collectAsStateWithLifecycle()
    val allTechnicians by viewModel.allTechnicians.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var newZoneText by remember { mutableStateOf("") }
    var newZoneError by remember { mutableStateOf<String?>(null) }

    // Deletion confirmation state
    var zonePendingDelete by remember { mutableStateOf<Zone?>(null) }
    var assignedTechsForDelete by remember { mutableStateOf<List<Technician>>(emptyList()) }
    var removeFromProfilesChoice by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(IHCPrimaryGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = IHCPrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Salem Zone Management",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Text(
                        text = "Add, review, and safely remove service localities",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Add Zone Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAF9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Add New Locality / Zone",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newZoneText,
                            onValueChange = {
                                newZoneText = it
                                if (newZoneError != null) newZoneError = null
                            },
                            placeholder = { Text("e.g. Steel Plant, Kannankurichi") },
                            singleLine = true,
                            isError = newZoneError != null,
                            colors = appTextFieldColors(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_zone_input")
                        )
                        Button(
                            onClick = {
                                val trimmed = newZoneText.trim()
                                if (trimmed.isEmpty()) {
                                    newZoneError = "Enter zone name"
                                } else {
                                    viewModel.addZone(trimmed) { success ->
                                        if (success) {
                                            newZoneText = ""
                                            newZoneError = null
                                        } else {
                                            newZoneError = "Zone already exists"
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IHCPrimaryGreen),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("confirm_add_zone_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add")
                        }
                    }
                    if (newZoneError != null) {
                        Text(
                            text = newZoneError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Zone List
            Text(
                text = "Configured Zones (${zones.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = IHCNavyBlue
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                items(zones, key = { it.id }) { zone ->
                    // Calculate assigned technicians count
                    val assignedCount = remember(zone.name, allTechnicians) {
                        val cleanName = zone.name.trim().lowercase()
                        allTechnicians.count { tech ->
                            tech.zonesList.any { it.trim().lowercase() == cleanName }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = zone.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = IHCNavyBlue
                            )
                            Text(
                                text = if (assignedCount == 0) "No technicians assigned" else "$assignedCount technician(s) assigned",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (assignedCount > 0) IHCPrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val assigned = viewModel.getTechniciansAssignedToZone(zone.name)
                                    assignedTechsForDelete = assigned
                                    removeFromProfilesChoice = false
                                    zonePendingDelete = zone
                                }
                            },
                            modifier = Modifier.testTag("delete_zone_${zone.name}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete ${zone.name}",
                                tint = IHCRed.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = IHCNavyBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }

    // Safety Confirmation Dialog for Zone Deletion
    zonePendingDelete?.let { zone ->
        AlertDialog(
            onDismissRequest = { zonePendingDelete = null },
            icon = {
                Icon(
                    imageVector = if (assignedTechsForDelete.isNotEmpty()) Icons.Default.Warning else Icons.Default.Delete,
                    contentDescription = null,
                    tint = if (assignedTechsForDelete.isNotEmpty()) IHCAmber else IHCRed
                )
            },
            title = {
                Text(
                    text = if (assignedTechsForDelete.isNotEmpty()) "Zone In Use by Technicians" else "Delete Zone?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (assignedTechsForDelete.isEmpty()) {
                    Text(
                        text = "Are you sure you want to remove '${zone.name}'? This locality will no longer appear in the quick zone selection chips.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Column {
                        Text(
                            text = "'${zone.name}' is currently assigned to ${assignedTechsForDelete.size} technician(s):",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = IHCNavyBlue
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = assignedTechsForDelete.take(5).joinToString(", ") { "${it.fullName} (${it.technicianId})" } +
                                if (assignedTechsForDelete.size > 5) " and ${assignedTechsForDelete.size - 5} more" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "How would you like to handle this?",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )

                        // Option 1
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            RadioButton(
                                selected = !removeFromProfilesChoice,
                                onClick = { removeFromProfilesChoice = false },
                                colors = RadioButtonDefaults.colors(selectedColor = IHCPrimaryGreen)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Keep zone in existing technician profiles, but remove from future list",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Option 2
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            RadioButton(
                                selected = removeFromProfilesChoice,
                                onClick = { removeFromProfilesChoice = true },
                                colors = RadioButtonDefaults.colors(selectedColor = IHCRed)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Safely remove this zone from their profiles and delete zone",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toDelete = zone
                        val removeFromTechs = removeFromProfilesChoice
                        zonePendingDelete = null
                        viewModel.deleteZone(toDelete, removeFromTechs) {}
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (assignedTechsForDelete.isNotEmpty() && removeFromProfilesChoice) IHCRed else IHCPrimaryGreen
                    ),
                    modifier = Modifier.testTag("confirm_delete_zone_button")
                ) {
                    Text("Confirm Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { zonePendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
