package com.njit.medtrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.njit.medtrack.Medication

@Database(
    entities = [Medication::class, SymptomEntry::class, DoseLog::class, MedicalProfile::class],
    version = 4,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao
    abstract fun healthLogDao(): HealthLogDao
    abstract fun doseLogDao(): DoseLogDao
    abstract fun medicalProfileDao(): MedicalProfileDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medtrack.db",
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
    }
}
