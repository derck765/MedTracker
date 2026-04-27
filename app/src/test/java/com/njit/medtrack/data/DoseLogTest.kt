package com.njit.medtrack.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DoseLogTest {

    @Test
    fun defaultIdIsZero() {
        val log = DoseLog(medicationId = 5L, scheduledFor = 1000L, takenAt = 2000L)
        assertEquals(0L, log.id)
    }

    @Test
    fun fieldsAreAssigned() {
        val log = DoseLog(id = 7L, medicationId = 5L, scheduledFor = 1000L, takenAt = 2000L)
        assertEquals(7L, log.id)
        assertEquals(5L, log.medicationId)
        assertEquals(1000L, log.scheduledFor)
        assertEquals(2000L, log.takenAt)
    }

    @Test
    fun copyOnlyChangesProvidedField() {
        val log = DoseLog(medicationId = 1, scheduledFor = 100, takenAt = 200)
        val updated = log.copy(takenAt = 999)
        assertEquals(1L, updated.medicationId)
        assertEquals(100L, updated.scheduledFor)
        assertEquals(999L, updated.takenAt)
    }
}
