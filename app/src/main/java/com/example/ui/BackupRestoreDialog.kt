package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.util.BackupValidationResult
import com.example.ui.theme.IHCAmber
import com.example.ui.theme.IHCAmberLight
import com.example.ui.theme.IHCNavyBlue
import com.example.ui.theme.IHCPrimaryGreen
import com.example.ui.theme.IHCPrimaryGreenLight
import com.example.ui.theme.IHCRed
import com.example.ui.theme.IHCRedLight

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreSheet(
    totalCurrentRecords: Int,
    onExportBackupClicked: () -> Unit,
    onImportBackupClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    tint = IHCPrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Backup & Restore Database",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Text(
                        text = "Current database contains $totalCurrentRecords technician record(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Export Card
            Card(
                colors = CardDefaults.cardColors(containerColor = IHCPrimaryGreenLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Export Backup (JSON)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Save all technician profiles, contacts, and notes into an offline JSON backup file.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            onExportBackupClicked()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IHCPrimaryGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_backup_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Choose Location & Export Backup", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Import Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Restore from Backup",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Select an existing InstaHomeCare backup JSON file. You will preview and confirm before any data changes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onImportBackupClicked()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_backup_button")
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Select Backup File to Restore", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun RestorePreviewDialog(
    result: BackupValidationResult,
    currentDatabaseCount: Int,
    onConfirmRestore: (replaceAll: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var replaceAll by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            if (result.isValid) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IHCPrimaryGreen)
            } else {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = IHCRed)
            }
        },
        title = {
            Text(
                text = if (result.isValid) "Validate & Restore Backup" else "Invalid Backup File",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (!result.isValid) {
                Column {
                    Text(
                        text = result.errorMessage ?: "The selected file cannot be validated as a valid InstaHomeCare backup.",
                        color = IHCRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Column {
                    Text(
                        text = "Backup Verified Successfully",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCPrimaryGreen
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("• App: ${result.appName}")
                    Text("• Exported on: ${result.exportedAtFormatted}")
                    Text("• Records found in file: ${result.recordCount}")
                    Text("• Current database records: $currentDatabaseCount")

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Restore Mode:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = IHCNavyBlue
                    )

                    // Mode 1: Merge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        RadioButton(
                            selected = !replaceAll,
                            onClick = { replaceAll = false },
                            colors = RadioButtonDefaults.colors(selectedColor = IHCPrimaryGreen)
                        )
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text("Merge with existing data", fontWeight = FontWeight.SemiBold)
                            Text("Adds new technicians; skips duplicates by ID/phone", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Mode 2: Replace
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        RadioButton(
                            selected = replaceAll,
                            onClick = { replaceAll = true },
                            colors = RadioButtonDefaults.colors(selectedColor = IHCRed)
                        )
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text("Replace all existing data", fontWeight = FontWeight.SemiBold, color = IHCRed)
                            Text("Erases current database and replaces with this backup", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (replaceAll) {
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            color = IHCRedLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = IHCRed, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Warning: $currentDatabaseCount existing record(s) will be permanently replaced.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = IHCRed
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (result.isValid) {
                Button(
                    onClick = {
                        onConfirmRestore(replaceAll)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (replaceAll) IHCRed else IHCPrimaryGreen
                    ),
                    modifier = Modifier.testTag("confirm_restore_button")
                ) {
                    Text(if (replaceAll) "Proceed & Replace All" else "Merge Records")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
