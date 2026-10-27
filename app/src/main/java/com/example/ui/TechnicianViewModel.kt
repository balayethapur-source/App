package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Technician
import com.example.data.TechnicianRepository
import com.example.util.BackupValidationResult
import com.example.util.CsvExporter
import com.example.util.JsonBackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class FilterState(
    val trade: String? = null,
    val joiningInterest: String? = null,
    val minExperienceYears: Float? = null,
    val workingHours: String? = null,
    val serviceAreaType: String? = null,
    val specificZone: String? = null,
    val verificationStatus: String? = null
) {
    val activeCount: Int
        get() {
            var count = 0
            if (trade != null) count++
            if (joiningInterest != null) count++
            if (minExperienceYears != null && minExperienceYears > 0f) count++
            if (workingHours != null) count++
            if (serviceAreaType != null) count++
            if (specificZone != null) count++
            if (verificationStatus != null) count++
            return count
        }

    val isDefault: Boolean
        get() = activeCount == 0
}

data class DashboardStats(
    val totalCount: Int = 0,
    val electricians: Int = 0,
    val plumbers: Int = 0,
    val housekeeping: Int = 0,
    val others: Int = 0,
    val verifiedCount: Int = 0,
    val interestedCount: Int = 0
)

class TechnicianViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TechnicianRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TechnicianRepository(database.technicianDao(), database.zoneDao())
    }

    val allTechnicians: StateFlow<List<Technician>> = repository.allTechnicians
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allZones: StateFlow<List<com.example.data.Zone>> = repository.allZones
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val searchQuery = MutableStateFlow("")
    val filterState = MutableStateFlow(FilterState())

    val filteredTechnicians: StateFlow<List<Technician>> = combine(
        allTechnicians,
        searchQuery,
        filterState
    ) { list, query, filters ->
        val cleanQuery = query.trim().lowercase(Locale.getDefault())

        list.filter { tech ->
            // Search query matches: name, phone, alternate phone, tech ID, town, trade, zones, notes
            val matchesQuery = cleanQuery.isEmpty() ||
                tech.fullName.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.mobileNumber.contains(cleanQuery) ||
                tech.alternateMobileNumber.contains(cleanQuery) ||
                tech.technicianId.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.currentTown.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.hometown.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.trade.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.tradeOther.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.additionalSkills.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.preferredZones.lowercase(Locale.getDefault()).contains(cleanQuery) ||
                tech.addressLocality.lowercase(Locale.getDefault()).contains(cleanQuery)

            if (!matchesQuery) return@filter false

            // Trade filter (matches any of the technician's selected categories)
            if (filters.trade != null) {
                if (!tech.matchesCategory(filters.trade)) {
                    return@filter false
                }
            }

            // Joining interest filter
            if (filters.joiningInterest != null && !tech.interestedInJoining.equals(filters.joiningInterest, ignoreCase = true)) {
                return@filter false
            }

            // Experience filter
            if (filters.minExperienceYears != null && tech.experienceYears < filters.minExperienceYears) {
                return@filter false
            }

            // Working hours filter
            if (filters.workingHours != null && !tech.preferredWorkingHours.equals(filters.workingHours, ignoreCase = true)) {
                return@filter false
            }

            // Service area type filter (Entire Salem city vs Selected zones only)
            if (filters.serviceAreaType != null && !tech.preferredServiceArea.equals(filters.serviceAreaType, ignoreCase = true)) {
                return@filter false
            }

            // Specific zone filter
            if (filters.specificZone != null && filters.specificZone.isNotBlank()) {
                val matchZone = tech.preferredServiceArea.equals("Entire Salem city", ignoreCase = true) ||
                    tech.zonesList.any { it.contains(filters.specificZone, ignoreCase = true) }
                if (!matchZone) return@filter false
            }

            // Status filter
            if (filters.verificationStatus != null && !tech.followUpStatus.equals(filters.verificationStatus, ignoreCase = true)) {
                return@filter false
            }

            true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats: StateFlow<DashboardStats> = allTechnicians.combine(MutableStateFlow(Unit)) { list, _ ->
        var elec = 0
        var plum = 0
        var house = 0
        var other = 0
        var verified = 0
        var interested = 0

        for (t in list) {
            if (t.matchesCategory("Electrician")) elec++
            if (t.matchesCategory("Plumber")) plum++
            if (t.matchesCategory("Housekeeping")) house++
            if (t.tradesList.any { !listOf("electrician", "plumber", "housekeeping").contains(it.lowercase(Locale.getDefault())) }) {
                other++
            }
            if (t.followUpStatus.equals("Verified", ignoreCase = true)) verified++
            if (t.interestedInJoining.equals("Yes", ignoreCase = true)) interested++
        }

        DashboardStats(
            totalCount = list.size,
            electricians = elec,
            plumbers = plum,
            housekeeping = house,
            others = other,
            verifiedCount = verified,
            interestedCount = interested
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStats()
    )

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun updateFilters(update: (FilterState) -> FilterState) {
        filterState.value = update(filterState.value)
    }

    fun clearAllFilters() {
        filterState.value = FilterState()
        searchQuery.value = ""
    }

    suspend fun checkDuplicatePhone(phone: String, excludeId: Long = 0L): List<Technician> {
        return repository.checkDuplicatePhone(phone, excludeId)
    }

    fun findTechniciansForCategory(category: String, zone: String? = null): List<Technician> {
        return allTechnicians.value.filter { tech ->
            val matchesCat = tech.matchesCategory(category)
            val matchesZone = zone.isNullOrBlank() ||
                tech.preferredServiceArea.equals("Entire Salem city", ignoreCase = true) ||
                tech.zonesList.any { it.contains(zone, ignoreCase = true) }
            matchesCat && matchesZone
        }
    }

    suspend fun generateNextTechnicianId(): String {
        return repository.generateNextTechnicianId()
    }

    fun addZone(name: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.addZone(name)
            onResult(success)
        }
    }

    suspend fun getTechniciansAssignedToZone(zoneName: String): List<Technician> {
        return repository.getTechniciansAssignedToZone(zoneName)
    }

    fun deleteZone(
        zone: com.example.data.Zone,
        removeFromAssignedTechnicians: Boolean,
        onResult: () -> Unit
    ) {
        viewModelScope.launch {
            repository.deleteZone(zone, removeFromAssignedTechnicians)
            onResult()
        }
    }

    fun insertTechnician(technician: Technician, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insert(technician)
            onResult(id)
        }
    }

    fun updateTechnician(technician: Technician, onResult: () -> Unit) {
        viewModelScope.launch {
            repository.update(technician)
            onResult()
        }
    }

    fun deleteTechnician(technician: Technician, onResult: () -> Unit) {
        viewModelScope.launch {
            repository.delete(technician)
            onResult()
        }
    }

    fun exportToCsv(
        uri: Uri,
        onSuccess: (count: Int) -> Unit,
        onError: (message: String) -> Unit
    ) {
        viewModelScope.launch {
            val list = repository.getAllSnapshot()
            val result = CsvExporter.exportToUri(getApplication(), uri, list)
            if (result.isSuccess) {
                onSuccess(result.getOrDefault(list.size))
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "CSV export failed")
            }
        }
    }

    fun exportBackup(
        uri: Uri,
        onSuccess: (count: Int) -> Unit,
        onError: (message: String) -> Unit
    ) {
        viewModelScope.launch {
            val list = repository.getAllSnapshot()
            val result = JsonBackupManager.exportBackup(getApplication(), uri, list)
            if (result.isSuccess) {
                onSuccess(result.getOrDefault(list.size))
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Backup failed")
            }
        }
    }

    fun validateBackup(
        uri: Uri,
        onResult: (BackupValidationResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = JsonBackupManager.validateAndParseBackup(getApplication(), uri)
            onResult(result)
        }
    }

    fun restoreBackup(
        technicians: List<Technician>,
        replaceAll: Boolean,
        onSuccess: (inserted: Int, skipped: Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (inserted, skipped) = repository.importTechnicians(technicians, replaceAll)
                onSuccess(inserted, skipped)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to restore backup")
            }
        }
    }
}
