package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.Technician
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /**
     * Exports list of technicians into a UTF-8 with BOM CSV format compatible with Microsoft Excel.
     * Preserves phone numbers and Tamil / Unicode characters.
     */
    suspend fun exportToUri(
        context: Context,
        uri: Uri,
        technicians: List<Technician>
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                // Write UTF-8 BOM so Excel opens Tamil and Unicode characters accurately
                outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    // CSV Headers
                    val headers = listOf(
                        "Technician ID",
                        "Full Name",
                        "Mobile Number",
                        "Alternate Mobile Number",
                        "Hometown",
                        "Current Town",
                        "Address / Locality",
                        "Trade",
                        "Trade Details (Other)",
                        "Additional Skills",
                        "Experience (Years)",
                        "Current Occupation",
                        "Own Vehicle",
                        "Tools Available",
                        "Interested in Joining InstaHomeCare",
                        "Preferred Working Hours",
                        "Preferred Service Area",
                        "Preferred Zones / Localities",
                        "Date Met",
                        "Follow-up / Verification Status",
                        "Additional Notes",
                        "Created At",
                        "Last Updated"
                    )
                    writer.write(headers.joinToString(separator = ",", postfix = "\r\n") { escapeCsv(it) })

                    for (tech in technicians) {
                        val row = listOf(
                            escapeCsv(tech.technicianId),
                            escapeCsv(tech.fullName),
                            formatPhoneForExcel(tech.mobileNumber),
                            formatPhoneForExcel(tech.alternateMobileNumber),
                            escapeCsv(tech.hometown),
                            escapeCsv(tech.currentTown),
                            escapeCsv(tech.addressLocality),
                            escapeCsv(tech.trade),
                            escapeCsv(tech.tradeOther),
                            escapeCsv(tech.additionalSkills),
                            escapeCsv(tech.experienceYears.toString()),
                            escapeCsv(tech.currentOccupation),
                            escapeCsv(tech.ownVehicle),
                            escapeCsv(tech.toolsAvailable),
                            escapeCsv(tech.interestedInJoining),
                            escapeCsv(tech.preferredWorkingHours),
                            escapeCsv(tech.preferredServiceArea),
                            escapeCsv(tech.preferredZones),
                            escapeCsv(tech.dateMet),
                            escapeCsv(tech.followUpStatus),
                            escapeCsv(tech.additionalNotes),
                            escapeCsv(dateFormat.format(Date(tech.createdAt))),
                            escapeCsv(dateFormat.format(Date(tech.updatedAt)))
                        )
                        writer.write(row.joinToString(separator = ",", postfix = "\r\n"))
                    }
                    writer.flush()
                }
            } ?: return@withContext Result.failure(Exception("Could not open file output stream"))

            Result.success(technicians.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Escapes fields with quotes and commas according to standard CSV RFC 4180.
     */
    fun escapeCsv(value: String?): String {
        if (value.isNullOrEmpty()) return "\"\""
        var text = value.replace("\"", "\"\"")
        return "\"$text\""
    }

    /**
     * Formats phone number as an Excel text formula =\"0994535111\" so Excel never trims leading
     * zeros or converts large phone numbers into scientific notation.
     */
    fun formatPhoneForExcel(phone: String?): String {
        val clean = phone?.trim().orEmpty()
        if (clean.isEmpty()) return "\"\""
        // In CSV, ="9994535111" escaped with quotes is "=""9994535111"""
        return "\"=\"\"$clean\"\"\""
    }
}
