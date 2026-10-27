package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Locale

class TechnicianRepository(
    private val technicianDao: TechnicianDao,
    private val zoneDao: ZoneDao
) {

    val allTechnicians: Flow<List<Technician>> = technicianDao.getAllTechnicians()
    val totalCount: Flow<Int> = technicianDao.getCount()
    val allZones: Flow<List<Zone>> = zoneDao.getAllZones()

    fun getTechnicianById(id: Long): Flow<Technician?> {
        return technicianDao.getTechnicianById(id)
    }

    suspend fun getTechnicianByIdSync(id: Long): Technician? = withContext(Dispatchers.IO) {
        technicianDao.getTechnicianByIdSync(id)
    }

    suspend fun getAllSnapshot(): List<Technician> = withContext(Dispatchers.IO) {
        technicianDao.getAllTechniciansSnapshot()
    }

    suspend fun getAllZonesSnapshot(): List<Zone> = withContext(Dispatchers.IO) {
        val list = zoneDao.getAllZonesSnapshot()
        if (list.isEmpty()) {
            val defaults = DEFAULT_SALEM_ZONES.map { Zone(name = it, isCustom = false) }
            zoneDao.insertAll(defaults)
            defaults
        } else {
            list
        }
    }

    suspend fun addZone(rawName: String): Boolean = withContext(Dispatchers.IO) {
        val clean = rawName.trim()
        if (clean.isEmpty()) return@withContext false
        val existing = zoneDao.getAllZonesSnapshot()
        if (existing.any { it.name.equals(clean, ignoreCase = true) }) {
            return@withContext false
        }
        zoneDao.insert(Zone(name = clean, isCustom = true))
        true
    }

    suspend fun getTechniciansAssignedToZone(zoneName: String): List<Technician> = withContext(Dispatchers.IO) {
        val clean = zoneName.trim().lowercase(Locale.getDefault())
        technicianDao.getAllTechniciansSnapshot().filter { tech ->
            tech.zonesList.any { it.trim().lowercase(Locale.getDefault()) == clean }
        }
    }

    suspend fun deleteZone(zone: Zone, removeFromAssignedTechnicians: Boolean) = withContext(Dispatchers.IO) {
        val zoneNameClean = zone.name.trim().lowercase(Locale.getDefault())

        if (removeFromAssignedTechnicians) {
            val assignedTechs = technicianDao.getAllTechniciansSnapshot().filter { tech ->
                tech.zonesList.any { it.trim().lowercase(Locale.getDefault()) == zoneNameClean }
            }
            for (tech in assignedTechs) {
                val updatedZones = tech.zonesList
                    .filter { it.trim().lowercase(Locale.getDefault()) != zoneNameClean }
                    .joinToString(", ")
                technicianDao.update(tech.copy(preferredZones = updatedZones, updatedAt = System.currentTimeMillis()))
            }
        }

        zoneDao.delete(zone)
    }

    suspend fun checkDuplicatePhone(phone: String, excludeId: Long = 0L): List<Technician> =
        withContext(Dispatchers.IO) {
            val cleanPhone = phone.trim()
            if (cleanPhone.length < 5) return@withContext emptyList()
            if (excludeId > 0) {
                technicianDao.findByPhoneExceptId(cleanPhone, excludeId)
            } else {
                technicianDao.findByPhone(cleanPhone)
            }
        }

    suspend fun generateNextTechnicianId(): String = withContext(Dispatchers.IO) {
        val all = technicianDao.getAllTechniciansSnapshot()
        var maxNum = 0
        for (item in all) {
            val match = Regex("""IHC-(\d+)""", RegexOption.IGNORE_CASE).find(item.technicianId)
            if (match != null) {
                val num = match.groupValues[1].toIntOrNull() ?: 0
                if (num > maxNum) {
                    maxNum = num
                }
            }
        }
        val next = maxNum + 1
        String.format(Locale.US, "IHC-%04d", next)
    }

    suspend fun insert(technician: Technician): Long = withContext(Dispatchers.IO) {
        technicianDao.insert(technician)
    }

    suspend fun update(technician: Technician) = withContext(Dispatchers.IO) {
        technicianDao.update(technician.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(technician: Technician) = withContext(Dispatchers.IO) {
        technicianDao.delete(technician)
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        technicianDao.deleteAll()
    }

    suspend fun importTechnicians(
        incoming: List<Technician>,
        replaceAll: Boolean
    ): Pair<Int, Int> = withContext(Dispatchers.IO) {
        if (replaceAll) {
            technicianDao.deleteAll()
            technicianDao.insertAll(incoming)
            Pair(incoming.size, 0)
        } else {
            var inserted = 0
            var skipped = 0
            val existing = technicianDao.getAllTechniciansSnapshot()
            val existingTechIds = existing.map { it.technicianId.trim().uppercase(Locale.US) }.toSet()
            val existingPhones = existing.map { it.mobileNumber.trim() }.filter { it.isNotEmpty() }.toSet()

            val toInsert = mutableListOf<Technician>()
            for (tech in incoming) {
                val techIdUpper = tech.technicianId.trim().uppercase(Locale.US)
                val phoneClean = tech.mobileNumber.trim()
                if (existingTechIds.contains(techIdUpper) || (phoneClean.isNotEmpty() && existingPhones.contains(phoneClean))) {
                    skipped++
                } else {
                    toInsert.add(tech.copy(id = 0))
                    inserted++
                }
            }
            if (toInsert.isNotEmpty()) {
                technicianDao.insertAll(toInsert)
            }
            Pair(inserted, skipped)
        }
    }
}
