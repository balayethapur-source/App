package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Locale

val STANDARD_SERVICE_CATEGORIES = listOf(
    "Electrician",
    "Plumber",
    "Housekeeping",
    "Carpenter",
    "AC Technician",
    "Painter",
    "Appliance Repair",
    "Other"
)

@Entity(
    tableName = "technicians",
    indices = [
        Index(value = ["technicianId"], unique = true),
        Index(value = ["mobileNumber"])
    ]
)
data class Technician(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val technicianId: String,                       // e.g. IHC-0001
    val fullName: String,                           // Required
    val mobileNumber: String,                       // Required, preserves leading zeros
    val alternateMobileNumber: String = "",         // Optional
    val hometown: String = "",                      // Hometown
    val currentTown: String = "Salem",              // Current town
    val addressLocality: String = "",               // Address / locality
    val trade: String,                              // Comma-separated service categories e.g. "Electrician, Plumber"
    val tradeOther: String = "",                    // If trade contains Other
    val additionalSkills: String = "",              // Additional skills
    val experienceYears: Float = 0f,                // Professional experience in years
    val currentOccupation: String = "",             // Current occupation
    val ownVehicle: String = "Not specified",       // Yes / No / Not specified
    val toolsAvailable: String = "Not specified",   // Yes / Some / No / Not specified
    val interestedInJoining: String = "Undecided",  // Yes / No / Undecided
    val preferredWorkingHours: String = "Available whenever called, subject to confirmation",
    val preferredServiceArea: String = "Entire Salem city", // Entire Salem city / Selected zones only
    val preferredZones: String = "",                // Comma-separated zones/localities
    val dateMet: String = "",                       // Date met (YYYY-MM-DD or formatted)
    val followUpStatus: String = "Pending verification", // Verified, Pending verification, Follow-up required, On hold, Rejected
    val additionalNotes: String = "",               // Additional notes
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Splits comma-separated trades/categories into a list, safely migrating and preserving single or multi-categories
     */
    val tradesList: List<String>
        get() = if (trade.isBlank()) {
            emptyList()
        } else {
            trade.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

    /**
     * Checks if this technician supports a particular service category (case-insensitive)
     */
    fun matchesCategory(category: String): Boolean {
        val clean = category.trim().lowercase(Locale.getDefault())
        return tradesList.any { it.lowercase(Locale.getDefault()) == clean } ||
            (clean == "other" && tradeOther.isNotBlank()) ||
            (tradeOther.isNotBlank() && tradeOther.lowercase(Locale.getDefault()).contains(clean))
    }

    /**
     * Display name for trade(s), including custom specification if 'Other'
     */
    val displayTrade: String
        get() {
            val list = tradesList
            if (list.isEmpty()) return trade
            return list.joinToString(", ") { item ->
                if (item.equals("Other", ignoreCase = true) && tradeOther.isNotBlank()) {
                    "Other ($tradeOther)"
                } else {
                    item
                }
            }
        }

    /**
     * Splits comma-separated preferred zones into a clean list
     */
    val zonesList: List<String>
        get() = if (preferredZones.isBlank()) {
            emptyList()
        } else {
            preferredZones.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
}
