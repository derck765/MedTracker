package com.njit.medtrack.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.njit.medtrack.Medication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/**
 * End-to-end smoke test: inserts data through every DAO on a single
 * [AppDatabase] and reads it back. Verifies wiring of entities, type
 * converters, and DAO routing.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseSmokeTest {

    @get:Rule val dbRule = DatabaseTestRule()

    @Test
    fun appContextIsCorrect() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.njit.medtrack", ctx.packageName)
    }

    @Test
    fun allDaosAreReachable() {
        assertNotNull(dbRule.db.medicationDao())
        assertNotNull(dbRule.db.healthLogDao())
        assertNotNull(dbRule.db.doseLogDao())
        assertNotNull(dbRule.db.medicalProfileDao())
    }

    @Test
    fun crossDao_writeAndReadFlow() = runTest {
        val medDao = dbRule.db.medicationDao()
        val doseDao = dbRule.db.doseLogDao()
        val healthDao = dbRule.db.healthLogDao()
        val profileDao = dbRule.db.medicalProfileDao()

        val medId = medDao.insert(
            Medication(
                name = "Lisinopril",
                dosage = "20 mg",
                scheduleTime = "8:00 AM",
                tags = listOf("morning"),
                notes = "",
            ),
        )
        val now = System.currentTimeMillis()
        doseDao.insert(DoseLog(medicationId = medId, scheduledFor = now, takenAt = now))
        healthDao.insert(SymptomEntry(date = Date(now), symptoms = "fine", severity = 1, notes = ""))
        profileDao.upsert(MedicalProfile(fullName = "Patient Zero"))

        assertEquals(1, medDao.getAll().size)
        assertEquals(1, doseDao.watchLogsForDay(now - 1, now + 1).first().size)
        assertEquals(1, healthDao.watchAll().first().size)
        assertEquals("Patient Zero", profileDao.watch().first()?.fullName)
    }
}
