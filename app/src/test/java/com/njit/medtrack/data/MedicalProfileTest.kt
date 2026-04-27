package com.njit.medtrack.data

import org.junit.Assert.assertEquals
import org.junit.Test

class MedicalProfileTest {

    @Test
    fun defaultsAreEmpty() {
        val p = MedicalProfile()
        assertEquals(1, p.id)
        assertEquals("", p.fullName)
        assertEquals("", p.dateOfBirth)
        assertEquals("", p.bloodType)
        assertEquals("", p.allergies)
        assertEquals("", p.conditions)
        assertEquals("", p.emergencyContact)
        assertEquals("", p.emergencyPhone)
        assertEquals("", p.insuranceProvider)
        assertEquals("", p.insuranceId)
    }

    @Test
    fun copyPreservesIdAndUpdatesFields() {
        val updated = MedicalProfile().copy(
            fullName = "John Doe",
            bloodType = "O+",
            allergies = "Peanuts",
        )
        assertEquals(1, updated.id)
        assertEquals("John Doe", updated.fullName)
        assertEquals("O+", updated.bloodType)
        assertEquals("Peanuts", updated.allergies)
        assertEquals("", updated.dateOfBirth)
    }
}
