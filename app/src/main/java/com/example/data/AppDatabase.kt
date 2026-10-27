package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val DEFAULT_SALEM_ZONES = listOf(
    "Hasthampatti",
    "Ammapet",
    "Suramangalam",
    "Kondalampatti",
    "Shevapet",
    "Fairlands",
    "Alagapuram",
    "Salem Junction",
    "Four Roads",
    "Gugai",
    "Attur",
    "Omalur"
)

@Database(entities = [Technician::class, Zone::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun technicianDao(): TechnicianDao
    abstract fun zoneDao(): ZoneDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "instahomecare_technicians.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            prepopulateZones(context)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            prepopulateZones(context)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun prepopulateZones(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { db ->
                    val count = db.zoneDao().getCount()
                    if (count == 0) {
                        val zones = DEFAULT_SALEM_ZONES.map { Zone(name = it, isCustom = false) }
                        db.zoneDao().insertAll(zones)
                    }
                }
            }
        }
    }
}
