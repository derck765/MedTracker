package com.njit.medtrack.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class HealthLogDaoTest {

    @get:Rule val dbRule = DatabaseTestRule()
    private val dao get() = dbRule.db.healthLogDao()

    private fun entry(
        date: Date = Date(),
        symptoms: String = "headache",
        severity: Int = 3,
        notes: String = "",
    ) = SymptomEntry(date = date, symptoms = symptoms, severity = severity, notes = notes)

    @Test
    fun insert_andWatchAll_returnsEntry() = runTest {
        dao.insert(entry(symptoms = "fever", severity = 5, notes = "drank water"))

        val list = dao.watchAll().first()
        assertEquals(1, list.size)
        assertEquals("fever", list.single().symptoms)
        assertEquals(5, list.single().severity)
    }

    @Test
    fun watchAll_isSortedByDateDescending() = runTest {
        val older = Date(1_700_000_000_000L)
        val newer = Date(1_700_500_000_000L)
        val newest = Date(1_701_000_000_000L)

        dao.insert(entry(date = older, symptoms = "old"))
        dao.insert(entry(date = newest, symptoms = "newest"))
        dao.insert(entry(date = newer, symptoms = "newer"))

        val list = dao.watchAll().first()
        assertEquals(listOf("newest", "newer", "old"), list.map { it.symptoms })
    }

    @Test
    fun update_modifiesExistingEntry() = runTest {
        dao.insert(entry(symptoms = "before", severity = 1))
        val original = dao.watchAll().first().single()

        dao.update(original.copy(symptoms = "after", severity = 8, notes = "updated"))

        val updated = dao.watchAll().first().single()
        assertEquals(original.id, updated.id)
        assertEquals("after", updated.symptoms)
        assertEquals(8, updated.severity)
        assertEquals("updated", updated.notes)
    }

    @Test
    fun deleteById_onlyRemovesTargetRow() = runTest {
        dao.insert(entry(symptoms = "keep"))
        dao.insert(entry(symptoms = "drop"))
        val all = dao.watchAll().first()
        assertEquals(2, all.size)

        val toDelete = all.first { it.symptoms == "drop" }
        dao.deleteById(toDelete.id)

        val remaining = dao.watchAll().first()
        assertEquals(1, remaining.size)
        assertEquals("keep", remaining.single().symptoms)
    }

    @Test
    fun typeConverters_persistDateThroughDb() = runTest {
        val date = Date(1_700_000_000_000L)
        dao.insert(entry(date = date))

        val stored = dao.watchAll().first().single()
        assertEquals(date, stored.date)
        assertTrue(stored.id > 0L)
    }
}
