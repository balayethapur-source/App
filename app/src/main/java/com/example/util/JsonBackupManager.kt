package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.Technician
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupValidationResult(
    val isValid: Boolean,
    val appName: String,
    val exportedAtFormatted: String,
    val recordCount: Int,
    val technicians: List<Technician>,
    val errorMessage: String? = null
)

object JsonBackupManager {

    private const val APP_KEY = "InstaHomeCare Technician Manager"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    suspend fun exportBackup(
        context: Context,
        uri: Uri,
        technicians: List<Technician>
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject()
            root.put("app", APP_KEY)
            root.put("version", 1)
            root.put("exportedAt", System.currentTimeMillis())
            root.put("exportedAtFormatted", dateFormat.format(Date()))
            root.put("recordCount", technicians.size)

            val array = JSONArray()
            for (tech in technicians) {
                val obj = JSONObject()
                obj.put("technicianId", tech.technicianId)
                obj.put("fullName", tech.fullName)
                obj.put("mobileNumber", tech.mobileNumber)
                obj.put("alternateMobileNumber", tech.alternateMobileNumber)
                obj.put("hometown", tech.hometown)
                obj.put("currentTown", tech.currentTown)
                obj.put("addressLocality", tech.addressLocality)
                obj.put("trade", tech.trade)
                obj.put("tradeOther", tech.tradeOther)
                obj.put("additionalSkills", tech.additionalSkills)
                obj.put("experienceYears", tech.experienceYears.toDouble())
                obj.put("currentOccupation", tech.currentOccupation)
                obj.put("ownVehicle", tech.ownVehicle)
                obj.put("toolsAvailable", tech.toolsAvailable)
                obj.put("interestedInJoining", tech.interestedInJoining)
                obj.put("preferredWorkingHours", tech.preferredWorkingHours)
                obj.put("preferredServiceArea", tech.preferredServiceArea)
                obj.put("preferredZones", tech.preferredZones)
                obj.put("dateMet", tech.dateMet)
                obj.put("followUpStatus", tech.followUpStatus)
                obj.put("additionalNotes", tech.additionalNotes)
                obj.put("createdAt", tech.createdAt)
                obj.put("updatedAt", tech.updatedAt)
                array.put(obj)
            }
            root.put("technicians", array)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    writer.write(root.toString(2))
                    writer.flush()
                }
            } ?: return@withContext Result.failure(Exception("Cannot open destination file"))

            Result.success(technicians.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun validateAndParseBackup(
        context: Context,
        uri: Uri
    ): BackupValidationResult = withContext(Dispatchers.IO) {
        try {
            val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    reader.readText()
                }
            } ?: return@withContext BackupValidationResult(
                isValid = false,
                appName = "",
                exportedAtFormatted = "",
                recordCount = 0,
                technicians = emptyList(),
                errorMessage = "Failed to read backup file stream."
            )

            val root = JSONObject(content)
            val app = root.optString("app", "")
            if (!app.contains("InstaHomeCare", ignoreCase = true)) {
                return@withContext BackupValidationResult(
                    isValid = false,
                    appName = app,
                    exportedAtFormatted = "",
                    recordCount = 0,
                    technicians = emptyList(),
                    errorMessage = "Selected file is not an InstaHomeCare backup. Found: '$app'"
                )
            }

            val array = root.optJSONArray("technicians")
            if (array == null) {
                return@withContext BackupValidationResult(
                    isValid = false,
                    appName = app,
                    exportedAtFormatted = "",
                    recordCount = 0,
                    technicians = emptyList(),
                    errorMessage = "Backup file is missing technicians data array."
                )
            }

            val parsed = mutableListOf<Technician>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val techId = obj.optString("technicianId", "")
                val name = obj.optString("fullName", "")
                val phone = obj.optString("mobileNumber", "")

                if (name.isBlank() || phone.isBlank()) {
                    continue // Skip corrupt or incomplete record
                }

                val tech = Technician(
                    id = 0,
                    technicianId = techId.ifBlank { "IHC-" + (i + 1) },
                    fullName = name,
                    mobileNumber = phone,
                    alternateMobileNumber = obj.optString("alternateMobileNumber", ""),
                    hometown = obj.optString("hometown", ""),
                    currentTown = obj.optString("currentTown", "Salem"),
                    addressLocality = obj.optString("addressLocality", ""),
                    trade = obj.optString("trade", "Other"),
                    tradeOther = obj.optString("tradeOther", ""),
                    additionalSkills = obj.optString("additionalSkills", ""),
                    experienceYears = obj.optDouble("experienceYears", 0.0).toFloat(),
                    currentOccupation = obj.optString("currentOccupation", ""),
                    ownVehicle = obj.optString("ownVehicle", "Not specified"),
                    toolsAvailable = obj.optString("toolsAvailable", "Not specified"),
                    interestedInJoining = obj.optString("interestedInJoining", "Undecided"),
                    preferredWorkingHours = obj.optString("preferredWorkingHours", "Available whenever called, subject to confirmation"),
                    preferredServiceArea = obj.optString("preferredServiceArea", "Entire Salem city"),
                    preferredZones = obj.optString("preferredZones", ""),
                    dateMet = obj.optString("dateMet", ""),
                    followUpStatus = obj.optString("followUpStatus", "Pending verification"),
                    additionalNotes = obj.optString("additionalNotes", ""),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                )
                parsed.add(tech)
            }

            BackupValidationResult(
                isValid = true,
                appName = app,
                exportedAtFormatted = root.optString("exportedAtFormatted", "Unknown date"),
                recordCount = parsed.size,
                technicians = parsed,
                errorMessage = null
            )
        } catch (e: Exception) {
            BackupValidationResult(
                isValid = false,
                appName = "",
                exportedAtFormatted = "",
                recordCount = 0,
                technicians = emptyList(),
                errorMessage = "File parse error: ${e.localizedMessage ?: "Invalid JSON format"}"
            )
        }
    }
}
