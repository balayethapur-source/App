package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "zones",
    indices = [Index(value = ["name"], unique = true)]
)
data class Zone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
