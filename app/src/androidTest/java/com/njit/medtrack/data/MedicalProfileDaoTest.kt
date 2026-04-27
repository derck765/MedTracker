package com.njit.medtrack.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MedicalProfileDaoTest {

    @get:Rule val dbRule = DatabaseTestRule()
    private val dao get() = dbRule.db.medicalProfileDao()

    @Test
    fun watch_emitsNull_whenNoProfileExists() = runTest {
        assertNull(dao.watch().first())
    }

    @Test
    fun upsert_insertsProfile_thenWatchReturnsIt() = runTest {
        val profile = MedicalProfile(
            fullName = "John Doe",
            dateOfBirth = "1990-01-01",
            bloodType = "O+",
            allergies = "Peanuts",
            conditions = "Asthma",
            emergencyContact = "Jane Doe",
            emergencyPhone = "555-0100",
            insuranceProvider = "Acme",
            insuranceId = "ABC123",
        )

        dao.upsert(profile)

        val stored = dao.watch().first()
        assertEquals(profile, stored)
    }

    @Test
    fun upsert_replacesExistingProfile_keepingSingletonId() = runTest {
        dao.upsert(MedicalProfile(fullName = "Original", bloodType = "A+"))
        dao.upsert(MedicalProfile(fullName = "Updated", bloodType = "B-"))

        val stored = dao.watch().first()
        assertEquals(1, stored?.id)
        assertEquals("Updated", stored?.fullName)
        assertEquals("B-", stored?.bloodType)
    }
}
