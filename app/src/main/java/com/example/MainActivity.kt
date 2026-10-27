package com.example

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Technician
import com.example.ui.BackupRestoreSheet
import com.example.ui.HomeScreen
import com.example.ui.RestorePreviewDialog
import com.example.ui.TechnicianDetailScreen
import com.example.ui.TechnicianFormScreen
import com.example.ui.TechnicianViewModel
import com.example.ui.theme.InstaHomeCareTheme
import com.example.util.BackupValidationResult
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    HOME,
    ADD_TECHNICIAN,
    EDIT_TECHNICIAN,
    TECHNICIAN_DETAIL
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            InstaHomeCareTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: TechnicianViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedTechnician by remember { mutableStateOf<Technician?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showBackupRestoreSheet by remember { mutableStateOf(false) }
    var restoreValidationResult by remember { mutableStateOf<BackupValidationResult?>(null) }

    // SAF Launchers
    // 1. CSV Export
    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.exportToCsv(
                uri = uri,
                onSuccess = { count ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Successfully exported $count technician record(s) to CSV")
                    }
                },
                onError = { err ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("CSV Export error: $err")
                    }
                }
            )
        }
    }

    // 2. JSON Backup Export
    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.exportBackup(
                uri = uri,
                onSuccess = { count ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Backup created with $count technician record(s)")
                    }
                },
                onError = { err ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Backup export error: $err")
                    }
                }
            )
        }
    }

    // 3. JSON Backup Import / Restore
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.validateBackup(uri) { validationResult ->
                restoreValidationResult = validationResult
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToAdd = {
                            selectedTechnician = null
                            currentScreen = AppScreen.ADD_TECHNICIAN
                        },
                        onNavigateToDetail = { tech ->
                            selectedTechnician = tech
                            currentScreen = AppScreen.TECHNICIAN_DETAIL
                        },
                        onExportCsvClicked = {
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                            exportCsvLauncher.launch("InstaHomeCare_Technicians_$timeStamp.csv")
                        },
                        onOpenBackupRestore = {
                            showBackupRestoreSheet = true
                        }
                    )
                }

                AppScreen.ADD_TECHNICIAN -> {
                    TechnicianFormScreen(
                        viewModel = viewModel,
                        existingTechnician = null,
                        onNavigateBack = { currentScreen = AppScreen.HOME },
                        onSavedSuccessfully = { techId ->
                            currentScreen = AppScreen.HOME
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Technician $techId added successfully!")
                            }
                        }
                    )
                }

                AppScreen.EDIT_TECHNICIAN -> {
                    TechnicianFormScreen(
                        viewModel = viewModel,
                        existingTechnician = selectedTechnician,
                        onNavigateBack = { currentScreen = AppScreen.TECHNICIAN_DETAIL },
                        onSavedSuccessfully = { techId ->
                            currentScreen = AppScreen.HOME
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Technician $techId updated successfully!")
                            }
                        }
                    )
                }

                AppScreen.TECHNICIAN_DETAIL -> {
                    selectedTechnician?.let { tech ->
                        TechnicianDetailScreen(
                            technician = tech,
                            onNavigateBack = { currentScreen = AppScreen.HOME },
                            onEdit = { currentScreen = AppScreen.EDIT_TECHNICIAN },
                            onDelete = {
                                viewModel.deleteTechnician(tech) {
                                    currentScreen = AppScreen.HOME
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Deleted ${tech.fullName} (${tech.technicianId})")
                                    }
                                }
                            }
                        )
                    } ?: run {
                        currentScreen = AppScreen.HOME
                    }
                }
            }
        }
    }

    // Backup & Restore Sheet
    if (showBackupRestoreSheet) {
        BackupRestoreSheet(
            totalCurrentRecords = viewModel.allTechnicians.value.size,
            onExportBackupClicked = {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                exportBackupLauncher.launch("InstaHomeCare_Backup_$timeStamp.json")
            },
            onImportBackupClicked = {
                importBackupLauncher.launch(arrayOf("application/json", "*/*"))
            },
            onDismiss = { showBackupRestoreSheet = false }
        )
    }

    // Restore Validation & Confirmation Dialog
    restoreValidationResult?.let { result ->
        RestorePreviewDialog(
            result = result,
            currentDatabaseCount = viewModel.allTechnicians.value.size,
            onConfirmRestore = { replaceAll ->
                viewModel.restoreBackup(
                    technicians = result.technicians,
                    replaceAll = replaceAll,
                    onSuccess = { inserted, skipped ->
                        restoreValidationResult = null
                        coroutineScope.launch {
                            val msg = if (replaceAll) {
                                "Database replaced with $inserted technician record(s)"
                            } else {
                                "Restored: $inserted added, $skipped duplicate(s) skipped"
                            }
                            snackbarHostState.showSnackbar(msg)
                        }
                    },
                    onError = { err ->
                        restoreValidationResult = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Restore error: $err")
                        }
                    }
                )
            },
            onDismiss = { restoreValidationResult = null }
        )
    }
}
