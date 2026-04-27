package com.njit.medtrack.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DoseLogDaoTest {

    @get:Rule val dbRule = DatabaseTestRule()
    private val dao get() = dbRule.db.doseLogDao()

    private val dayStart = 1_700_000_000_000L
    private val dayEnd = dayStart + 24L * 60 * 60 * 1000

    @Test
    fun insert_returnsRowId_andLogIsVisibleForThatDay() = runTest {
        val id = dao.insert(
            DoseLog(
                medicationId = 1L,
                scheduledFor = dayStart + 1_000,
                takenAt = dayStart + 2_000,
            ),
        )
        assertTrue(id > 0L)

        val list = dao.watchLogsForDay(dayStart, dayEnd).first()
        assertEquals(1, list.size)
        assertEquals(1L, list.single().medicationId)
    }

    @Test
    fun insert_duplicateMedicationAndScheduledFor_isIgnored() = runTest {
        val first = dao.insert(
            DoseLog(medicationId = 1L, scheduledFor = dayStart, takenAt = dayStart),
        )
        val second = dao.insert(
            DoseLog(medicationId = 1L, scheduledFor = dayStart, takenAt = dayStart + 5_000),
        )

        assertTrue(first > 0L)
        // OnConflictStrategy.IGNORE returns -1 when the row is dropped.
        assertEquals(-1L, second)

        val list = dao.watchLogsForDay(dayStart, dayEnd).first()
        assertEquals(1, list.size)
    }

    @Test
    fun watchLogsForDay_excludesEntriesOutsideRange() = runTest {
        // Before window
        dao.insert(DoseLog(medicationId = 1L, scheduledFor = dayStart - 1, takenAt = dayStart))
        // At end (exclusive)
        dao.insert(DoseLog(medicationId = 2L, scheduledFor = dayEnd, takenAt = dayEnd))
        // Inside window
        dao.insert(DoseLog(medicationId = 3L, scheduledFor = dayStart + 5_000, takenAt = dayStart + 5_000))

        val list = dao.watchLogsForDay(dayStart, dayEnd).first()
        assertEquals(listOf(3L), list.map { it.medicationId })
    }

    @Test
    fun watchLogsForDay_isSortedByScheduledForAscending() = runTest {
        dao.insert(DoseLog(medicationId = 1L, scheduledFor = dayStart + 9_000, takenAt = dayStart))
        dao.insert(DoseLog(medicationId = 2L, scheduledFor = dayStart + 1_000, takenAt = dayStart))
        dao.insert(DoseLog(medicationId = 3L, scheduledFor = dayStart + 5_000, takenAt = dayStart))

        val list = dao.watchLogsForDay(dayStart, dayEnd).first()
        assertEquals(listOf(2L, 3L, 1L), list.map { it.medicationId })
    }
}
