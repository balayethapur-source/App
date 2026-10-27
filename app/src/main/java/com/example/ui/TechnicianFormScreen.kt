package com.example.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Technician
import com.example.ui.theme.IHCAmber
import com.example.ui.theme.IHCAmberLight
import com.example.ui.theme.IHCNavyBlue
import com.example.ui.theme.IHCPrimaryGreen
import com.example.ui.theme.IHCPrimaryGreenLight
import com.example.ui.theme.appTextFieldColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val WORKING_HOURS_OPTIONS = listOf(
    "Available whenever called, subject to confirmation",
    "Morning only",
    "Evening only",
    "Full-time",
    "Weekends only",
    "Call in advance to confirm",
    "Other"
)

private val VERIFICATION_STATUS_OPTIONS = listOf(
    "Pending verification",
    "Verified",
    "Follow-up required",
    "On hold",
    "Rejected"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TechnicianFormScreen(
    viewModel: TechnicianViewModel,
    existingTechnician: Technician? = null,
    onNavigateBack: () -> Unit,
    onSavedSuccessfully: (technicianId: String) -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val persistentZones by viewModel.allZones.collectAsStateWithLifecycle()

    val isEditing = existingTechnician != null
    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Form Fields State
    var technicianId by remember {
        mutableStateOf(existingTechnician?.technicianId ?: "Generating...")
    }
    var fullName by remember { mutableStateOf(existingTechnician?.fullName ?: "") }
    var mobileNumber by remember { mutableStateOf(existingTechnician?.mobileNumber ?: "") }
    var alternateMobileNumber by remember { mutableStateOf(existingTechnician?.alternateMobileNumber ?: "") }
    var hometown by remember { mutableStateOf(existingTechnician?.hometown ?: "") }
    var currentTown by remember { mutableStateOf(existingTechnician?.currentTown ?: "Salem") }
    var addressLocality by remember { mutableStateOf(existingTechnician?.addressLocality ?: "") }

    val selectedCategories = remember {
        mutableStateListOf<String>().apply {
            if (existingTechnician != null) {
                addAll(existingTechnician.tradesList)
            } else {
                add("Electrician")
            }
        }
    }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var tradeOther by remember { mutableStateOf(existingTechnician?.tradeOther ?: "") }
    var additionalSkills by remember { mutableStateOf(existingTechnician?.additionalSkills ?: "") }

    // Placeholder behaviour: Empty initial string for new records so placeholder disappears cleanly on typing
    var experienceYearsText by remember {
        mutableStateOf(
            existingTechnician?.experienceYears?.let {
                if (it == 0f) "" else if (it % 1 == 0f) it.toInt().toString() else it.toString()
            } ?: ""
        )
    }
    var currentOccupation by remember { mutableStateOf(existingTechnician?.currentOccupation ?: "") }

    var ownVehicle by remember { mutableStateOf(existingTechnician?.ownVehicle ?: "Not specified") }
    var toolsAvailable by remember { mutableStateOf(existingTechnician?.toolsAvailable ?: "Not specified") }
    var interestedInJoining by remember { mutableStateOf(existingTechnician?.interestedInJoining ?: "Undecided") }

    var preferredWorkingHours by remember {
        mutableStateOf(existingTechnician?.preferredWorkingHours ?: WORKING_HOURS_OPTIONS.first())
    }
    var hoursDropdownExpanded by remember { mutableStateOf(false) }

    var preferredServiceArea by remember {
        mutableStateOf(existingTechnician?.preferredServiceArea ?: "Entire Salem city")
    }

    val selectedZones = remember {
        mutableStateListOf<String>().apply {
            if (existingTechnician != null && existingTechnician.preferredZones.isNotBlank()) {
                addAll(existingTechnician.zonesList)
            }
        }
    }
    var customZoneInput by remember { mutableStateOf("") }
    var showZoneManagementSheet by remember { mutableStateOf(false) }

    // Date Met and Date Picker state
    var dateMet by remember { mutableStateOf(existingTechnician?.dateMet?.ifBlank { todayDate } ?: todayDate) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    var followUpStatus by remember {
        mutableStateOf(existingTechnician?.followUpStatus ?: "Pending verification")
    }
    var statusDropdownExpanded by remember { mutableStateOf(false) }
    var additionalNotes by remember { mutableStateOf(existingTechnician?.additionalNotes ?: "") }

    // Validation & Duplicate Checks
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var duplicatePhoneWarning by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    var contactTargetField by remember { mutableStateOf<String?>(null) }
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = result.data?.data
            contactUri?.let { uri ->
                try {
                    val projection = arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                    val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
                    cursor?.let { c ->
                        try {
                            if (c.moveToFirst()) {
                                val numberCol = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                val nameCol = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                val rawNumber = if (numberCol >= 0) c.getString(numberCol) else null
                                val contactName = if (nameCol >= 0) c.getString(nameCol) else null

                                val cleanNumber = rawNumber?.replace(Regex("[^0-9+]"), "")?.trim().orEmpty()

                                if (contactTargetField == "alternate") {
                                    if (cleanNumber.isNotBlank()) {
                                        alternateMobileNumber = cleanNumber
                                    }
                                } else {
                                    if (cleanNumber.isNotBlank()) {
                                        mobileNumber = cleanNumber
                                        if (phoneError != null) phoneError = null
                                    }
                                    if (fullName.isBlank() && !contactName.isNullOrBlank()) {
                                        fullName = contactName.trim()
                                        if (nameError != null) nameError = null
                                    }
                                }
                            }
                        } finally {
                            c.close()
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    val openContactPicker: (String) -> Unit = { target ->
        contactTargetField = target
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        contactPickerLauncher.launch(intent)
    }

    // Auto-generate Technician ID for new records
    LaunchedEffect(isEditing) {
        if (!isEditing && technicianId == "Generating...") {
            technicianId = viewModel.generateNextTechnicianId()
        }
    }

    // Check duplicate phone number reactively
    LaunchedEffect(mobileNumber) {
        val clean = mobileNumber.trim()
        if (clean.length >= 7) {
            val dupes = viewModel.checkDuplicatePhone(clean, excludeId = existingTechnician?.id ?: 0L)
            if (dupes.isNotEmpty()) {
                val match = dupes.first()
                duplicatePhoneWarning = "Notice: Number already registered to ${match.fullName} (${match.technicianId})"
            } else {
                duplicatePhoneWarning = null
            }
        } else {
            duplicatePhoneWarning = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isEditing) "Edit Technician" else "Add New Technician",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = IHCNavyBlue
                        )
                        Text(
                            text = if (isEditing) "ID: $technicianId" else "InstaHomeCare Internal Record",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("form_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = IHCNavyBlue
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
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("form_cancel_button")
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            var hasError = false
                            if (fullName.trim().isEmpty()) {
                                nameError = "Full name is required"
                                hasError = true
                            } else {
                                nameError = null
                            }

                            val cleanPhone = mobileNumber.trim()
                            if (cleanPhone.isEmpty()) {
                                phoneError = "Mobile number is required"
                                hasError = true
                            } else if (cleanPhone.length < 7) {
                                phoneError = "Enter valid mobile number"
                                hasError = true
                            } else {
                                phoneError = null
                            }

                            if (selectedCategories.isEmpty()) {
                                categoryError = "Please select at least one service category"
                                hasError = true
                            } else {
                                categoryError = null
                            }

                            if (!hasError && !isSaving) {
                                isSaving = true
                                val expFloat = experienceYearsText.toFloatOrNull() ?: 0f
                                val zonesString = selectedZones.joinToString(", ")
                                val tradeString = selectedCategories.joinToString(", ")

                                val record = Technician(
                                    id = existingTechnician?.id ?: 0L,
                                    technicianId = technicianId,
                                    fullName = fullName.trim(),
                                    mobileNumber = cleanPhone,
                                    alternateMobileNumber = alternateMobileNumber.trim(),
                                    hometown = hometown.trim(),
                                    currentTown = currentTown.trim().ifBlank { "Salem" },
                                    addressLocality = addressLocality.trim(),
                                    trade = tradeString,
                                    tradeOther = if (selectedCategories.contains("Other")) tradeOther.trim() else "",
                                    additionalSkills = additionalSkills.trim(),
                                    experienceYears = expFloat,
                                    currentOccupation = currentOccupation.trim(),
                                    ownVehicle = ownVehicle,
                                    toolsAvailable = toolsAvailable,
                                    interestedInJoining = interestedInJoining,
                                    preferredWorkingHours = preferredWorkingHours,
                                    preferredServiceArea = preferredServiceArea,
                                    preferredZones = zonesString,
                                    dateMet = dateMet.trim(),
                                    followUpStatus = followUpStatus,
                                    additionalNotes = additionalNotes.trim(),
                                    createdAt = existingTechnician?.createdAt ?: System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )

                                if (isEditing) {
                                    viewModel.updateTechnician(record) {
                                        isSaving = false
                                        onSavedSuccessfully(record.technicianId)
                                    }
                                } else {
                                    viewModel.insertTechnician(record) {
                                        isSaving = false
                                        onSavedSuccessfully(record.technicianId)
                                    }
                                }
                            }
                        },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IHCPrimaryGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("save_technician_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Update Technician" else "Save Technician",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
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
            // Header: Auto-Generated ID Card
            Card(
                colors = CardDefaults.cardColors(containerColor = IHCPrimaryGreenLight),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TECHNICIAN ID",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = IHCPrimaryGreen
                        )
                        Text(
                            text = technicianId,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = IHCNavyBlue
                        )
                    }
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IHCPrimaryGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isEditing) "Editing Record" else "Auto-Generated",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = IHCPrimaryGreen
                        )
                    }
                }
            }

            // Duplicate Phone Warning banner
            AnimatedVisibility(visible = duplicatePhoneWarning != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = IHCAmberLight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = IHCAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = duplicatePhoneWarning.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            // SECTION 1: Personal & Contact Information
            SectionCard(
                title = "Contact & Identity",
                icon = Icons.Default.Person
            ) {
                // Quick Contact Book Import Button
                OutlinedButton(
                    onClick = { openContactPicker("primary") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IHCNavyBlue),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, IHCPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("pick_contact_primary_banner_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = "Contact Book",
                        tint = IHCPrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Import Name & Number from Contacts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = IHCNavyBlue
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Full Name (Required)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        if (nameError != null && it.isNotBlank()) nameError = null
                    },
                    label = { Text("Full Name *") },
                    placeholder = { Text("e.g. R. Murugan") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    colors = appTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("full_name_input")
                )

                Spacer(Modifier.height(10.dp))

                // Mobile Number (Required)
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        mobileNumber = it
                        if (phoneError != null && it.isNotBlank()) phoneError = null
                    },
                    label = { Text("Mobile Number *") },
                    placeholder = { Text("e.g. 0994535111 or 9994535111") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = IHCPrimaryGreen)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { openContactPicker("primary") },
                            modifier = Modifier.testTag("pick_contact_mobile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContactPhone,
                                contentDescription = "Pick mobile from contacts",
                                tint = IHCPrimaryGreen
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError != null,
                    supportingText = {
                        if (phoneError != null) {
                            Text(phoneError!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Tap contact icon or type number")
                        }
                    },
                    singleLine = true,
                    colors = appTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mobile_number_input")
                )

                Spacer(Modifier.height(10.dp))

                // Alternate Mobile Number (Optional)
                OutlinedTextField(
                    value = alternateMobileNumber,
                    onValueChange = { alternateMobileNumber = it },
                    label = { Text("Alternate Mobile Number (Optional)") },
                    placeholder = { Text("e.g. 6379242552") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = IHCNavyBlue)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { openContactPicker("alternate") },
                            modifier = Modifier.testTag("pick_contact_alternate_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContactPhone,
                                contentDescription = "Pick alternate mobile from contacts",
                                tint = IHCNavyBlue
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = appTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("alternate_mobile_input")
                )

                Spacer(Modifier.height(10.dp))

                // Date Met with clickable Calendar Date Picker and Quick 'Today' button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dateMet,
                        onValueChange = { dateMet = it },
                        label = { Text("Date Met (YYYY-MM-DD)") },
                        placeholder = { Text("YYYY-MM-DD") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePickerDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Open Calendar",
                                    tint = IHCPrimaryGreen
                                )
                            }
                        },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("date_met_input")
                    )

                    Button(
                        onClick = { dateMet = todayDate },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IHCNavyBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("date_met_today_button")
                    ) {
                        Text(
                            text = "Today",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // SECTION 2: Location & Address
            SectionCard(
                title = "Location & Locality",
                icon = Icons.Default.LocationOn
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = currentTown,
                        onValueChange = { currentTown = it },
                        label = { Text("Current Town") },
                        placeholder = { Text("e.g. Salem") },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("current_town_input")
                    )

                    OutlinedTextField(
                        value = hometown,
                        onValueChange = { hometown = it },
                        label = { Text("Hometown") },
                        placeholder = { Text("e.g. Attur") },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hometown_input")
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = addressLocality,
                    onValueChange = { addressLocality = it },
                    label = { Text("Address / Locality") },
                    placeholder = { Text("e.g. No. 12, Gandhi Road, Suramangalam") },
                    minLines = 2,
                    maxLines = 3,
                    colors = appTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("address_locality_input")
                )
            }

            // SECTION 3: Trade & Professional Profile
            SectionCard(
                title = "Trade & Experience",
                icon = Icons.Default.Engineering
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Service Categories * (Select all that apply)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = IHCNavyBlue
                    )
                    if (selectedCategories.isNotEmpty()) {
                        Surface(
                            color = IHCPrimaryGreenLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${selectedCategories.size} selected",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IHCPrimaryGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.data.STANDARD_SERVICE_CATEGORIES.forEach { categoryName ->
                        val isChecked = selectedCategories.contains(categoryName)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (isChecked) {
                                    selectedCategories.remove(categoryName)
                                } else {
                                    selectedCategories.add(categoryName)
                                    categoryError = null
                                }
                            },
                            leadingIcon = if (isChecked) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                }
                            } else null,
                            label = {
                                Text(
                                    text = categoryName,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCPrimaryGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("category_chip_$categoryName")
                        )
                    }
                }

                if (categoryError != null) {
                    Text(
                        text = categoryError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                // If 'Other' trade is selected
                AnimatedVisibility(visible = selectedCategories.contains("Other")) {
                    Column {
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = tradeOther,
                            onValueChange = { tradeOther = it },
                            label = { Text("Specify Other Category *") },
                            placeholder = { Text("e.g. Mason, Welder, CCTV Technician") },
                            singleLine = true,
                            colors = appTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("trade_other_input")
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = additionalSkills,
                    onValueChange = { additionalSkills = it },
                    label = { Text("Additional Skills") },
                    placeholder = { Text("e.g. Inverter wiring, RO installation, Wall drilling") },
                    singleLine = true,
                    colors = appTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("additional_skills_input")
                )

                Spacer(Modifier.height(12.dp))

                // Experience in Years & Current Occupation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = experienceYearsText,
                        onValueChange = { experienceYearsText = it },
                        label = { Text("Experience (Years)") },
                        placeholder = { Text("e.g. 5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("experience_years_input")
                    )

                    OutlinedTextField(
                        value = currentOccupation,
                        onValueChange = { currentOccupation = it },
                        label = { Text("Current Occupation") },
                        placeholder = { Text("e.g. Freelance") },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("current_occupation_input")
                    )
                }

                Spacer(Modifier.height(12.dp))

                ChoiceSelectorRow(
                    label = "Own Vehicle:",
                    options = listOf("Yes", "No", "Not specified"),
                    selectedOption = ownVehicle,
                    onOptionSelected = { ownVehicle = it }
                )

                Spacer(Modifier.height(12.dp))

                ChoiceSelectorRow(
                    label = "Tools Available:",
                    options = listOf("Yes", "Some", "No", "Not specified"),
                    selectedOption = toolsAvailable,
                    onOptionSelected = { toolsAvailable = it }
                )
            }

            // SECTION 4: Alignment & Availability + Zone Management
            SectionCard(
                title = "InstaHomeCare Alignment & Service Area",
                icon = Icons.Default.Schedule
            ) {
                ChoiceSelectorRow(
                    label = "Interested in Joining IHC:",
                    options = listOf("Yes", "No", "Undecided"),
                    selectedOption = interestedInJoining,
                    onOptionSelected = { interestedInJoining = it }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Preferred Working Hours",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = IHCNavyBlue
                )
                Spacer(Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = hoursDropdownExpanded,
                    onExpandedChange = { hoursDropdownExpanded = !hoursDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = preferredWorkingHours,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hoursDropdownExpanded) },
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("preferred_hours_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = hoursDropdownExpanded,
                        onDismissRequest = { hoursDropdownExpanded = false }
                    ) {
                        WORKING_HOURS_OPTIONS.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    preferredWorkingHours = option
                                    hoursDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Preferred Service Area",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = IHCNavyBlue
                )
                Spacer(Modifier.height(4.dp))

                Column {
                    listOf("Entire Salem city", "Selected zones only").forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { preferredServiceArea = option }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = preferredServiceArea == option,
                                onClick = { preferredServiceArea = option },
                                colors = RadioButtonDefaults.colors(selectedColor = IHCPrimaryGreen)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Zone Management Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Preferred Zones / Localities:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )

                    TextButton(
                        onClick = { showZoneManagementSheet = true },
                        modifier = Modifier.testTag("manage_zones_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Manage Zones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Display all active persistent zones
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    persistentZones.forEach { zoneItem ->
                        val isChecked = selectedZones.contains(zoneItem.name)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (isChecked) selectedZones.remove(zoneItem.name) else selectedZones.add(zoneItem.name)
                            },
                            label = { Text(zoneItem.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCNavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    // Any extra zones previously entered on this record
                    selectedZones.filter { z -> persistentZones.none { it.name.equals(z, ignoreCase = true) } }.forEach { extraZone ->
                        FilterChip(
                            selected = true,
                            onClick = { selectedZones.remove(extraZone) },
                            label = { Text(extraZone, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IHCNavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Quick add zone field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customZoneInput,
                        onValueChange = { customZoneInput = it },
                        placeholder = { Text("Add custom zone or locality...") },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_zone_input")
                    )
                    IconButton(
                        onClick = {
                            val trimmed = customZoneInput.trim()
                            if (trimmed.isNotEmpty()) {
                                if (!selectedZones.contains(trimmed)) {
                                    selectedZones.add(trimmed)
                                }
                                viewModel.addZone(trimmed) {}
                                customZoneInput = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(IHCPrimaryGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add zone", tint = Color.White)
                    }
                }
            }

            // SECTION 5: Verification & Notes
            SectionCard(
                title = "Verification & Notes",
                icon = Icons.Default.Security
            ) {
                Text(
                    text = "Follow-up / Verification Status",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = IHCNavyBlue
                )
                Spacer(Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = statusDropdownExpanded,
                    onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = followUpStatus,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                        colors = appTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("verification_status_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = statusDropdownExpanded,
                        onDismissRequest = { statusDropdownExpanded = false }
                    ) {
                        VERIFICATION_STATUS_OPTIONS.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption) },
                                onClick = {
                                    followUpStatus = statusOption
                                    statusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { additionalNotes = it },
                    label = { Text("Additional Notes & Observations") },
                    placeholder = { Text("Met near Suramangalam junction. Has own 2-wheeler, good attitude.") },
                    minLines = 3,
                    maxLines = 5,
                    colors = appTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("additional_notes_input")
                )
            }

            // Policy Notice
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = IHCNavyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Confidential internal record. As per company policy, do not collect Aadhaar or bank account details.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // Material 3 DatePickerDialog
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            dateMet = sdf.format(Date(millis))
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("Select", fontWeight = FontWeight.Bold, color = IHCPrimaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Zone Management Bottom Sheet
    if (showZoneManagementSheet) {
        ZoneManagementBottomSheet(
            viewModel = viewModel,
            onDismiss = { showZoneManagementSheet = false }
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(IHCPrimaryGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = IHCPrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = IHCNavyBlue
                )
            }
            content()
        }
    }
}

@Composable
private fun ChoiceSelectorRow(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = IHCNavyBlue
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOption.equals(option, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onOptionSelected(option) },
                    label = { Text(option, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (option == "Yes") IHCPrimaryGreen else IHCNavyBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}
