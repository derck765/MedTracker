package com.njit.medtrack.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.ExternalResource

/**
 * Builds a fresh in-memory [AppDatabase] for each test. The DB lives only for
 * the duration of a single test; nothing is persisted to disk and there is no
 * leakage between tests.
 */
class DatabaseTestRule : ExternalResource() {

    lateinit var db: AppDatabase
        private set

    override fun before() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }

    override fun after() {
        db.close()
    }
}
