package com.njit.medtrack

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MedicationTest {

    private fun sample(
        id: Long = 0L,
        name: String = "Aspirin",
        dosage: String = "100 mg",
        scheduleTime: String = "8:00 AM",
        frequency: String = "Daily",
        tags: List<String> = listOf("morning"),
        notes: String = "with food",
    ) = Medication(id, name, dosage, scheduleTime, frequency, tags, notes)

    @Test
    fun defaultIdIsZero() {
        val med = Medication(name = "X", dosage = "1mg", scheduleTime = "9", tags = emptyList(), notes = "")
        assertEquals(0L, med.id)
    }

    @Test
    fun defaultFrequencyIsDaily() {
        val med = Medication(name = "X", dosage = "1mg", scheduleTime = "9", tags = emptyList(), notes = "")
        assertEquals("Daily", med.frequency)
    }

    @Test
    fun copyAssignsIdWithoutChangingOtherFields() {
        val original = sample()
        val withId = original.copy(id = 42L)
        assertEquals(42L, withId.id)
        assertEquals(original.name, withId.name)
        assertEquals(original.dosage, withId.dosage)
        assertEquals(original.tags, withId.tags)
    }

    @Test
    fun equalityIsValueBased() {
        assertEquals(sample(id = 1), sample(id = 1))
        assertNotEquals(sample(id = 1), sample(id = 2))
    }

    @Test
    fun tagsAreIndependentOfNotes() {
        val med = sample(tags = listOf("a", "b"), notes = "n")
        assertEquals(listOf("a", "b"), med.tags)
        assertEquals("n", med.notes)
    }
}
