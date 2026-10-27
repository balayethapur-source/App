package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianDao {

    @Query("SELECT * FROM technicians ORDER BY id DESC")
    fun getAllTechnicians(): Flow<List<Technician>>

    @Query("SELECT * FROM technicians ORDER BY id DESC")
    suspend fun getAllTechniciansSnapshot(): List<Technician>

    @Query("SELECT * FROM technicians WHERE id = :id LIMIT 1")
    fun getTechnicianById(id: Long): Flow<Technician?>

    @Query("SELECT * FROM technicians WHERE id = :id LIMIT 1")
    suspend fun getTechnicianByIdSync(id: Long): Technician?

    @Query("SELECT * FROM technicians WHERE technicianId = :techId LIMIT 1")
    suspend fun getTechnicianByTechId(techId: String): Technician?

    @Query("SELECT * FROM technicians WHERE mobileNumber = :phone OR alternateMobileNumber = :phone")
    suspend fun findByPhone(phone: String): List<Technician>

    @Query("SELECT * FROM technicians WHERE (mobileNumber = :phone OR alternateMobileNumber = :phone) AND id != :excludeId")
    suspend fun findByPhoneExceptId(phone: String, excludeId: Long): List<Technician>

    @Query("SELECT COUNT(*) FROM technicians")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM technicians ORDER BY id DESC LIMIT 1")
    suspend fun getLatestTechnician(): Technician?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(technician: Technician): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(technicians: List<Technician>)

    @Update
    suspend fun update(technician: Technician)

    @Delete
    suspend fun delete(technician: Technician)

    @Query("DELETE FROM technicians WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM technicians")
    suspend fun deleteAll()
}
