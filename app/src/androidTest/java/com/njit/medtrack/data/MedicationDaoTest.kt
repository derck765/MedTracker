package com.njit.medtrack.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.njit.medtrack.Medication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests that exercise [MedicationDao] against a real in-memory
 * Room database. These run only on a connected device / emulator and never
 * touch the user's actual `medtrack.db`.
 */
@RunWith(AndroidJUnit4::class)
class MedicationDaoTest {

    @get:Rule val dbRule = DatabaseTestRule()
    private val dao get() = dbRule.db.medicationDao()

    private fun med(
        name: String = "Aspirin",
        dosage: String = "100 mg",
        scheduleTime: String = "8:00 AM",
        frequency: String = "Daily",
        tags: List<String> = listOf("morning"),
        notes: String = "with food",
    ) = Medication(
        name = name,
        dosage = dosage,
        scheduleTime = scheduleTime,
        frequency = frequency,
        tags = tags,
        notes = notes,
    )

    @Test
    fun insert_returnsGeneratedId_andRowIsRetrievable() = runTest {
        val id = dao.insert(med(name = "Aspirin", tags = listOf("morning", "with-food")))

        assertTrue("Generated id should be > 0", id > 0L)
        val all = dao.getAll()
        assertEquals(1, all.size)
        val stored = all.single()
        assertEquals(id, stored.id)
        assertEquals("Aspirin", stored.name)
        assertEquals(listOf("morning", "with-food"), stored.tags)
    }

    @Test
    fun update_replacesExistingRow() = runTest {
        val id = dao.insert(med(name = "Old", dosage = "5 mg"))
        dao.update(
            Medication(
                id = id,
                name = "New",
                dosage = "10 mg",
                scheduleTime = "9:00 PM",
                frequency = "Twice Daily",
                tags = listOf("evening"),
                notes = "updated notes",
            ),
        )

        val stored = dao.getAll().single()
        assertEquals("New", stored.name)
        assertEquals("10 mg", stored.dosage)
        assertEquals("Twice Daily", stored.frequency)
        assertEquals(listOf("evening"), stored.tags)
        assertEquals("updated notes", stored.notes)
    }

    @Test
    fun deleteById_removesOnlyTargetRow() = runTest {
        val keepId = dao.insert(med(name = "Keep"))
        val dropId = dao.insert(med(name = "Drop"))

        dao.deleteById(dropId)

        val remaining = dao.getAll()
        assertEquals(1, remaining.size)
        assertEquals(keepId, remaining.single().id)
    }

    @Test
    fun watchAll_emitsRowsSortedByNameAscending() = runTest {
        dao.insert(med(name = "Zoloft"))
        dao.insert(med(name = "Advil"))
        dao.insert(med(name = "Metformin"))

        val list = dao.watchAll().first()

        assertEquals(listOf("Advil", "Metformin", "Zoloft"), list.map { it.name })
    }

    @Test
    fun typeConverters_persistTagsThroughDb() = runTest {
        dao.insert(med(name = "Multitag", tags = listOf("morning", "with-food", "before-bed")))

        val stored = dao.getAll().single()
        assertEquals(listOf("morning", "with-food", "before-bed"), stored.tags)
    }
}
