package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.ui.theme.IHCNavyBlue
import com.example.ui.theme.IHCPrimaryGreen

private val POPULAR_ZONES_FOR_FILTER = listOf(
    "Hasthampatti",
    "Ammapet",
    "Suramangalam",
    "Kondalampatti",
    "Shevapet",
    "Fairlands",
    "Alagapuram",
    "Salem Junction",
    "Four Roads",
    "Gugai",
    "Attur",
    "Omalur"
)

private val WORKING_HOURS_FILTER_OPTIONS = listOf(
    "Available whenever called, subject to confirmation",
    "Morning only",
    "Evening only",
    "Full-time",
    "Weekends only",
    "Call in advance to confirm"
)

private val STATUS_FILTER_OPTIONS = listOf(
    "Verified",
    "Pending verification",
    "Follow-up required",
    "On hold",
    "Rejected"
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterState,
    onApplyFilter: (FilterState) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var trade by remember { mutableStateOf(currentFilter.trade) }
    var joiningInterest by remember { mutableStateOf(currentFilter.joiningInterest) }
    var minExp by remember { mutableStateOf(currentFilter.minExperienceYears) }
    var hours by remember { mutableStateOf(currentFilter.workingHours) }
    var serviceArea by remember { mutableStateOf(currentFilter.serviceAreaType) }
    var zone by remember { mutableStateOf(currentFilter.specificZone) }
    var status by remember { mutableStateOf(currentFilter.verificationStatus) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = IHCPrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Search & Filter Technicians",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                }

                TextButton(
                    onClick = {
                        trade = null
                        joiningInterest = null
                        minExp = null
                        hours = null
                        serviceArea = null
                        zone = null
                        status = null
                        onClearAll()
                    },
                    modifier = Modifier.testTag("clear_filters_button")
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("Clear All")
                }
            }

            Spacer(Modifier.height(16.dp))

            // 1. Trade
            FilterSection(title = "Trade / Specialization") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.data.STANDARD_SERVICE_CATEGORIES.forEach { item ->
                        val isSelected = trade == item
                        FilterChip(
                            selected = isSelected,
                            onClick = { trade = if (isSelected) null else item },
                            label = { Text(item) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCPrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 2. Joining Interest
            FilterSection(title = "Interest in Joining InstaHomeCare") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Yes", "No", "Undecided").forEach { item ->
                        val isSelected = joiningInterest == item
                        FilterChip(
                            selected = isSelected,
                            onClick = { joiningInterest = if (isSelected) null else item },
                            label = { Text(item) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (item == "Yes") IHCPrimaryGreen else IHCNavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 3. Experience
            FilterSection(title = "Minimum Professional Experience") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Any" to null,
                        "1+ Years" to 1.0f,
                        "3+ Years" to 3.0f,
                        "5+ Years" to 5.0f,
                        "10+ Years" to 10.0f
                    ).forEach { (label, years) ->
                        val isSelected = minExp == years
                        FilterChip(
                            selected = isSelected,
                            onClick = { minExp = years },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCNavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 4. Working Hours / Availability
            FilterSection(title = "Availability / Working Hours") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WORKING_HOURS_FILTER_OPTIONS.forEach { opt ->
                        val isSelected = hours == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { hours = if (isSelected) null else opt },
                            label = { Text(opt, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCNavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 5. Service Area Type
            FilterSection(title = "Service Area Scope") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Entire Salem city", "Selected zones only").forEach { opt ->
                        val isSelected = serviceArea == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { serviceArea = if (isSelected) null else opt },
                            label = { Text(opt) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCPrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 6. Specific Zone Filter
            FilterSection(title = "Specific Salem Zone / Locality") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    POPULAR_ZONES_FOR_FILTER.forEach { z ->
                        val isSelected = zone == z
                        FilterChip(
                            selected = isSelected,
                            onClick = { zone = if (isSelected) null else z },
                            label = { Text(z, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCNavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 7. Status Filter
            FilterSection(title = "Verification / Follow-up Status") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    STATUS_FILTER_OPTIONS.forEach { st ->
                        val isSelected = status == st
                        FilterChip(
                            selected = isSelected,
                            onClick = { status = if (isSelected) null else st },
                            label = { Text(st) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (st == "Verified") IHCPrimaryGreen else IHCNavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        val newFilter = FilterState(
                            trade = trade,
                            joiningInterest = joiningInterest,
                            minExperienceYears = minExp,
                            workingHours = hours,
                            serviceAreaType = serviceArea,
                            specificZone = zone,
                            verificationStatus = status
                        )
                        onApplyFilter(newFilter)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IHCPrimaryGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("apply_filter_button")
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = IHCNavyBlue
        )
        Spacer(Modifier.height(6.dp))
        content()
    }
}
