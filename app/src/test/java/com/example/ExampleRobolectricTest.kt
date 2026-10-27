package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Technician
import com.example.data.TechnicianDao
import com.example.data.TechnicianRepository
import com.example.data.Zone
import com.example.data.ZoneDao
import com.example.util.CsvExporter
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: TechnicianDao
    private lateinit var zoneDao: ZoneDao
    private lateinit var repository: TechnicianRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.technicianDao()
        zoneDao = database.zoneDao()
        repository = TechnicianRepository(dao, zoneDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `verify app name resource`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("InstaHomeCare", appName)
    }

    @Test
    fun `multiple service category selection and matching`() = runBlocking {
        // Technician with both Electrician and Plumber
        val multiTech = Technician(
            technicianId = "IHC-0001",
            fullName = "S. Vijay",
            mobileNumber = "9994535111",
            trade = "Electrician, Plumber",
            preferredServiceArea = "Entire Salem city"
        )
        val id = repository.insert(multiTech)
        assertTrue(id > 0)

        val retrieved = repository.getAllSnapshot()[0]
        assertEquals(listOf("Electrician", "Plumber"), retrieved.tradesList)
        assertTrue(retrieved.matchesCategory("Electrician"))
        assertTrue(retrieved.matchesCategory("Plumber"))
        assertFalse(retrieved.matchesCategory("Housekeeping"))

        // Single trade backward compatibility test
        val oldSingleTech = Technician(
            technicianId = "IHC-0002",
            fullName = "M. Kumar",
            mobileNumber = "6379242552",
            trade = "Housekeeping"
        )
        repository.insert(oldSingleTech)

        val all = repository.getAllSnapshot()
        val kumar = all.first { it.technicianId == "IHC-0002" }
        assertEquals(listOf("Housekeeping"), kumar.tradesList)
        assertTrue(kumar.matchesCategory("Housekeeping"))
    }

    @Test
    fun `technician insert view and auto id generation`() = runBlocking {
        val firstId = repository.generateNextTechnicianId()
        assertEquals("IHC-0001", firstId)

        val tech = Technician(
            technicianId = firstId,
            fullName = "R. Murugan",
            mobileNumber = "0994535111",
            hometown = "Attur",
            currentTown = "Salem",
            addressLocality = "Suramangalam",
            trade = "Electrician, Plumber, Housekeeping",
            experienceYears = 5.5f,
            ownVehicle = "Yes",
            toolsAvailable = "Yes",
            interestedInJoining = "Yes",
            preferredWorkingHours = "Full-time",
            preferredServiceArea = "Entire Salem city",
            preferredZones = "Suramangalam, Fairlands",
            followUpStatus = "Verified"
        )
        val rowId = repository.insert(tech)
        assertTrue(rowId > 0)

        val secondId = repository.generateNextTechnicianId()
        assertEquals("IHC-0002", secondId)

        val list = repository.getAllSnapshot()
        assertEquals(1, list.size)
        assertEquals("R. Murugan", list[0].fullName)
        assertEquals("0994535111", list[0].mobileNumber)
        assertEquals(3, list[0].tradesList.size)
    }

    @Test
    fun `zone addition and safe removal with technician reassignment`() = runBlocking {
        // Add custom zone
        val added = repository.addZone("Kandhampatti")
        assertTrue(added)

        // Try adding duplicate
        val duplicateAdded = repository.addZone("Kandhampatti")
        assertFalse(duplicateAdded)

        // Assign technician to Kandhampatti
        val tech = Technician(
            technicianId = "IHC-0010",
            fullName = "Zone Test Tech",
            mobileNumber = "9998887776",
            trade = "Plumber",
            preferredZones = "Kandhampatti, Suramangalam"
        )
        repository.insert(tech)

        val assigned = repository.getTechniciansAssignedToZone("Kandhampatti")
        assertEquals(1, assigned.size)
        assertEquals("Zone Test Tech", assigned[0].fullName)

        // Safe delete zone and remove from profile
        val zone = repository.getAllZonesSnapshot().first { it.name == "Kandhampatti" }
        repository.deleteZone(zone, removeFromAssignedTechnicians = true)

        val updatedTech = repository.getAllSnapshot().first { it.technicianId == "IHC-0010" }
        assertEquals("Suramangalam", updatedTech.preferredZones)
    }

    @Test
    fun `duplicate phone number detection`() = runBlocking {
        val tech = Technician(
            technicianId = "IHC-0001",
            fullName = "K. Selvam",
            mobileNumber = "9994535111",
            alternateMobileNumber = "6379242552",
            trade = "Plumber"
        )
        repository.insert(tech)

        val dupesMain = repository.checkDuplicatePhone("9994535111")
        assertEquals(1, dupesMain.size)
        assertEquals("K. Selvam", dupesMain[0].fullName)

        val dupesAlt = repository.checkDuplicatePhone("6379242552")
        assertEquals(1, dupesAlt.size)

        val noDupes = repository.checkDuplicatePhone("9876543210")
        assertEquals(0, noDupes.size)
    }

    @Test
    fun `csv format preserves leading zero phone and escapes special characters`() {
        val escapedText = CsvExporter.escapeCsv("Address with, comma and \"quotes\"")
        assertEquals("\"Address with, comma and \"\"quotes\"\"\"", escapedText)

        val excelPhone = CsvExporter.formatPhoneForExcel("0994535111")
        assertEquals("\"=\"\"0994535111\"\"\"", excelPhone)
    }
}
