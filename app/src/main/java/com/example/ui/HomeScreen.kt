package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.STANDARD_SERVICE_CATEGORIES
import com.example.data.Technician
import com.example.ui.theme.IHCAmber
import com.example.ui.theme.IHCAmberLight
import com.example.ui.theme.IHCNavyBlue
import com.example.ui.theme.IHCNavyBlueLight
import com.example.ui.theme.IHCPrimaryGreen
import com.example.ui.theme.IHCPrimaryGreenLight
import com.example.ui.theme.IHCRed
import com.example.ui.theme.IHCRedLight
import com.example.ui.theme.appTextFieldColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: TechnicianViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Technician) -> Unit,
    onExportCsvClicked: () -> Unit,
    onOpenBackupRestore: () -> Unit
) {
    val context = LocalContext.current
    val technicians by viewModel.filteredTechnicians.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showZoneSheet by remember { mutableStateOf(false) }
    var showJobAssignmentSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAdd,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Technician", fontWeight = FontWeight.Bold) },
                containerColor = IHCPrimaryGreen,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_technician")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAF9)),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Official InstaHomeCare Branding Card
            item {
                Card(
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Official Brand Emblem (Clean, proportional, sharp)
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_logo_symbol),
                                    contentDescription = "InstaHomeCare Logo",
                                    modifier = Modifier.size(48.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "InstaHomeCare",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = IHCNavyBlue
                                )
                                Text(
                                    text = "Technician Manager • Salem",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = IHCPrimaryGreen
                                )
                                Text(
                                    text = "Small Jobs. Instant Solutions.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Zone Management icon
                            IconButton(
                                onClick = { showZoneSheet = true },
                                modifier = Modifier.testTag("home_zones_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Manage Zones",
                                    tint = IHCNavyBlue
                                )
                            }

                            // Backup & Restore button
                            IconButton(
                                onClick = onOpenBackupRestore,
                                modifier = Modifier.testTag("home_backup_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Backup,
                                    contentDescription = "Backup and Restore",
                                    tint = IHCNavyBlue
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F3))
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatPill(count = stats.totalCount, label = "Total Techs", highlightColor = IHCNavyBlue)
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFD0D7D2)))
                            StatPill(count = stats.electricians, label = "Electricians", highlightColor = IHCPrimaryGreen)
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFD0D7D2)))
                            StatPill(count = stats.plumbers, label = "Plumbers", highlightColor = Color(0xFF0284C7))
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFD0D7D2)))
                            StatPill(count = stats.housekeeping, label = "Housekeeping", highlightColor = Color(0xFF7C3AED))
                        }

                        Spacer(Modifier.height(14.dp))

                        // Action Buttons: Row 1 (Add Technician & Assign Job)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onNavigateToAdd,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IHCPrimaryGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("home_add_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Add Technician",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }

                            Button(
                                onClick = { showJobAssignmentSheet = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IHCNavyBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("home_assign_job_button")
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Assign Job",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Action Buttons: Row 2 (Excel Import / Export & Manage Zones)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onExportCsvClicked,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = IHCNavyBlue),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, IHCNavyBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp)
                                    .testTag("export_excel_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = IHCNavyBlue)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Excel Import / Export",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }

                            OutlinedButton(
                                onClick = { showZoneSheet = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = IHCPrimaryGreen),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, IHCPrimaryGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(44.dp)
                                    .testTag("dashboard_manage_zones_button")
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp), tint = IHCPrimaryGreen)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Zones",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar & Filter Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search by name, phone, categories, zone...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = IHCNavyBlue)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_technicians_input")
                    )

                    // Filter Button with Badge showing active filter count
                    Surface(
                        onClick = { showFilterSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (filterState.activeCount > 0) IHCPrimaryGreen else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (filterState.activeCount > 0) IHCPrimaryGreen else Color(0xFFD0D7D2)
                        ),
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("filter_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (filterState.activeCount > 0) {
                                BadgedBox(badge = {
                                    Badge(
                                        containerColor = IHCNavyBlue,
                                        contentColor = Color.White
                                    ) {
                                        Text("${filterState.activeCount}")
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = "Filters",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filters",
                                    tint = IHCNavyBlue
                                )
                            }
                        }
                    }
                }
            }

            // Quick Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currentTrade = filterState.trade
                    FilterChip(
                        selected = currentTrade == null,
                        onClick = { viewModel.updateFilters { it.copy(trade = null) } },
                        label = { Text("All (${stats.totalCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IHCNavyBlue,
                            selectedLabelColor = Color.White
                        )
                    )

                    STANDARD_SERVICE_CATEGORIES.forEach { category ->
                        val isSelected = currentTrade.equals(category, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.updateFilters {
                                    it.copy(trade = if (isSelected) null else category)
                                }
                            },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCPrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    // Verified quick chip
                    val isVerifiedSelected = filterState.verificationStatus == "Verified"
                    FilterChip(
                        selected = isVerifiedSelected,
                        onClick = {
                            viewModel.updateFilters {
                                it.copy(verificationStatus = if (isVerifiedSelected) null else "Verified")
                            }
                        },
                        label = { Text("Verified (${stats.verifiedCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IHCPrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Active Filters Banner
            if (filterState.activeCount > 0 || searchQuery.isNotBlank()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Showing ${technicians.size} result(s)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = IHCNavyBlue
                        )
                        Text(
                            text = "Reset Filters",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = IHCRed,
                            modifier = Modifier
                                .clickable { viewModel.clearAllFilters() }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Empty State
            if (technicians.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(IHCPrimaryGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = IHCPrimaryGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = if (stats.totalCount == 0) "No Technicians Registered Yet" else "No Matching Technicians",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = IHCNavyBlue
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = if (stats.totalCount == 0)
                                    "Tap 'Add Technician' below to record technician details after in-person meetings."
                                else
                                    "Try clearing search queries or removing filter tags to see more records.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            if (stats.totalCount == 0) {
                                Button(
                                    onClick = onNavigateToAdd,
                                    colors = ButtonDefaults.buttonColors(containerColor = IHCPrimaryGreen)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Add First Technician")
                                }
                            } else {
                                OutlinedButton(onClick = { viewModel.clearAllFilters() }) {
                                    Text("Clear All Filters")
                                }
                            }
                        }
                    }
                }
            } else {
                // List of Technicians
                items(technicians, key = { it.id }) { tech ->
                    TechnicianListItemCard(
                        technician = tech,
                        onCardClick = { onNavigateToDetail(tech) },
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${tech.mobileNumber.trim()}")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = filterState,
            onApplyFilter = { newFilter ->
                viewModel.filterState.value = newFilter
            },
            onClearAll = {
                viewModel.clearAllFilters()
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    if (showZoneSheet) {
        ZoneManagementBottomSheet(
            viewModel = viewModel,
            onDismiss = { showZoneSheet = false }
        )
    }

    if (showJobAssignmentSheet) {
        JobAssignmentBottomSheet(
            viewModel = viewModel,
            onNavigateToDetail = onNavigateToDetail,
            onDismiss = { showJobAssignmentSheet = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TechnicianListItemCard(
    technician: Technician,
    onCardClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onCardClick() }
            .testTag("tech_card_${technician.technicianId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: ID, All Category Badges, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = technician.technicianId,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = IHCNavyBlue,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Verification Pill
                Surface(
                    color = when (technician.followUpStatus) {
                        "Verified" -> IHCPrimaryGreenLight
                        "Follow-up required" -> IHCAmberLight
                        "Rejected" -> IHCRedLight
                        else -> Color(0xFFF1F5F9)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = technician.followUpStatus,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = when (technician.followUpStatus) {
                            "Verified" -> IHCPrimaryGreen
                            "Follow-up required" -> IHCAmber
                            "Rejected" -> IHCRed
                            else -> IHCNavyBlue
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Multi-Category Badges
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
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
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = IHCPrimaryGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Name & Call Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = technician.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = IHCPrimaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = technician.mobileNumber,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = IHCPrimaryGreen
                        )
                        if (technician.experienceYears > 0f) {
                            Text(
                                text = " • ${technician.experienceYears} yrs exp",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Call Button (Direct Phone Dialler)
                IconButton(
                    onClick = onCallClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(IHCPrimaryGreenLight)
                        .size(42.dp)
                        .testTag("card_call_button_${technician.technicianId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call ${technician.fullName}",
                        tint = IHCPrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Working Area (Location & Preferred Service Area / Zones)
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Working Area",
                    tint = IHCPrimaryGreen,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(Modifier.width(6.dp))
                val locationText = buildString {
                    append(technician.currentTown.ifBlank { "Salem" })
                    if (technician.addressLocality.isNotBlank()) {
                        append(" (${technician.addressLocality})")
                    }
                    if (technician.preferredServiceArea == "Entire Salem city") {
                        append(" • Entire Salem City")
                    } else if (technician.preferredZones.isNotBlank()) {
                        append(" • Zones: ${technician.preferredZones}")
                    }
                }
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp
                    ),
                    color = IHCNavyBlue
                )
            }

            Spacer(Modifier.height(6.dp))

            // Availability & Vehicle info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Availability",
                        tint = IHCNavyBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = technician.preferredWorkingHours,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        ),
                        color = Color(0xFF0F172A)
                    )
                }

                if (technician.ownVehicle == "Yes") {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = IHCPrimaryGreenLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = IHCPrimaryGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "Own Vehicle",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IHCPrimaryGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(count: Int, label: String, highlightColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = highlightColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
