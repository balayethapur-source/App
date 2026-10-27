package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Technician
import com.example.ui.theme.IHCAmber
import com.example.ui.theme.IHCAmberLight
import com.example.ui.theme.IHCNavyBlue
import com.example.ui.theme.IHCNavyBlueLight
import com.example.ui.theme.IHCPrimaryGreen
import com.example.ui.theme.IHCPrimaryGreenLight
import com.example.ui.theme.IHCRed
import com.example.ui.theme.IHCRedLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TechnicianDetailScreen(
    technician: Technician,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val formattedCreated = remember(technician.createdAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(technician.createdAt))
    }
    val formattedUpdated = remember(technician.updatedAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(technician.updatedAt))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = technician.technicianId,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = IHCNavyBlue
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("detail_edit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Technician",
                            tint = IHCPrimaryGreen
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("detail_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Technician",
                            tint = IHCRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Call Button via Phone dialler (ACTION_DIAL)
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${technician.mobileNumber.trim()}")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IHCPrimaryGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .testTag("call_technician_button")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Call ${technician.mobileNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // Edit button
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("bottom_edit_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Edit")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(IHCPrimaryGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Engineering,
                                contentDescription = null,
                                tint = IHCPrimaryGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = technician.fullName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = IHCNavyBlue
                            )
                            Spacer(Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                technician.tradesList.forEach { category ->
                                    val label = if (category.equals("Other", ignoreCase = true) && technician.tradeOther.isNotBlank()) {
                                        "Other (${technician.tradeOther})"
                                    } else {
                                        category
                                    }
                                    Surface(
                                        color = IHCPrimaryGreenLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = IHCPrimaryGreen,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Surface(
                                    color = when (technician.followUpStatus) {
                                        "Verified" -> IHCPrimaryGreenLight
                                        "Follow-up required" -> IHCAmberLight
                                        "Rejected" -> IHCRedLight
                                        else -> IHCNavyBlueLight
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = technician.followUpStatus,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = when (technician.followUpStatus) {
                                            "Verified" -> IHCPrimaryGreen
                                            "Follow-up required" -> IHCAmber
                                            "Rejected" -> IHCRed
                                            else -> IHCNavyBlue
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Quick metadata row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAF9))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickMetric(label = "Experience", value = "${technician.experienceYears} yrs")
                        QuickMetric(label = "Vehicle", value = technician.ownVehicle)
                        QuickMetric(label = "Tools", value = technician.toolsAvailable)
                        QuickMetric(label = "Interest", value = technician.interestedInJoining)
                    }
                }
            }

            // Contact & Location Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contact & Location",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Spacer(Modifier.height(12.dp))

                    DetailItem(
                        icon = Icons.Default.Phone,
                        label = "Mobile Number",
                        value = technician.mobileNumber,
                        isPhone = true,
                        onPhoneClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${technician.mobileNumber.trim()}")
                            }
                            context.startActivity(intent)
                        }
                    )

                    if (technician.alternateMobileNumber.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        DetailItem(
                            icon = Icons.Default.Phone,
                            label = "Alternate Mobile Number",
                            value = technician.alternateMobileNumber,
                            isPhone = true,
                            onPhoneClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${technician.alternateMobileNumber.trim()}")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    DetailItem(
                        icon = Icons.Default.LocationOn,
                        label = "Current Town",
                        value = technician.currentTown
                    )

                    if (technician.hometown.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        DetailItem(
                            icon = Icons.Default.Home,
                            label = "Hometown",
                            value = technician.hometown
                        )
                    }

                    if (technician.addressLocality.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        DetailItem(
                            icon = Icons.Default.LocationOn,
                            label = "Address / Locality",
                            value = technician.addressLocality
                        )
                    }
                }
            }

            // Skills & Work Profile
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Skills & Trade Profile",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Spacer(Modifier.height(12.dp))

                    DetailItem(
                        icon = Icons.Default.Engineering,
                        label = "Service Categories",
                        value = technician.displayTrade
                    )

                    if (technician.additionalSkills.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        DetailItem(
                            icon = Icons.Default.Build,
                            label = "Additional Skills",
                            value = technician.additionalSkills
                        )
                    }

                    if (technician.currentOccupation.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        DetailItem(
                            icon = Icons.Default.Engineering,
                            label = "Current Occupation",
                            value = technician.currentOccupation
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    DetailItem(
                        icon = Icons.Default.DirectionsCar,
                        label = "Own Vehicle",
                        value = technician.ownVehicle
                    )

                    Spacer(Modifier.height(10.dp))
                    DetailItem(
                        icon = Icons.Default.Build,
                        label = "Tools Available",
                        value = technician.toolsAvailable
                    )
                }
            }

            // Availability & Service Area
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Availability & Service Area",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Spacer(Modifier.height(12.dp))

                    DetailItem(
                        icon = Icons.Default.Schedule,
                        label = "Preferred Working Hours",
                        value = technician.preferredWorkingHours
                    )

                    Spacer(Modifier.height(10.dp))
                    DetailItem(
                        icon = Icons.Default.LocationOn,
                        label = "Preferred Service Area",
                        value = technician.preferredServiceArea
                    )

                    if (technician.zonesList.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Zones / Localities:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = IHCNavyBlue
                        )
                        Spacer(Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            technician.zonesList.forEach { zone ->
                                Surface(
                                    color = IHCNavyBlueLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = zone,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = IHCNavyBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interview & Record History
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Meeting & Record Info",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Spacer(Modifier.height(12.dp))

                    DetailItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "Date Met",
                        value = technician.dateMet.ifBlank { "Not recorded" }
                    )

                    if (technician.additionalNotes.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        DetailItem(
                            icon = Icons.Default.Info,
                            label = "Notes & Observations",
                            value = technician.additionalNotes
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Record Created: $formattedCreated\nLast Updated: $formattedUpdated",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }

    // Confirmation dialog before deleting
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = {
                Icon(Icons.Default.Delete, contentDescription = null, tint = IHCRed)
            },
            title = {
                Text("Delete Technician Record?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to delete ${technician.fullName} (${technician.technicianId})? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IHCRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuickMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = IHCNavyBlue
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    isPhone: Boolean = false,
    onPhoneClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isPhone) IHCPrimaryGreen else IHCNavyBlue,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isPhone && onPhoneClick != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = IHCPrimaryGreen
                    ),
                    modifier = Modifier.clickable { onPhoneClick() }
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = IHCNavyBlue
                )
            }
        }
    }
}
