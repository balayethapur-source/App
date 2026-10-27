package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {

    @Query("SELECT * FROM zones ORDER BY name ASC")
    fun getAllZones(): Flow<List<Zone>>

    @Query("SELECT * FROM zones ORDER BY name ASC")
    suspend fun getAllZonesSnapshot(): List<Zone>

    @Query("SELECT COUNT(*) FROM zones")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(zone: Zone): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(zones: List<Zone>)

    @Delete
    suspend fun delete(zone: Zone)

    @Query("DELETE FROM zones WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM zones WHERE LOWER(name) = LOWER(:name)")
    suspend fun deleteByName(name: String)
}
