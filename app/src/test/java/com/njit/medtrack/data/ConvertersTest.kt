package com.njit.medtrack.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

/**
 * Pure-JVM unit tests for the Room [Converters]. These verify the contract that
 * Room relies on when persisting / reading non-primitive entity fields.
 */
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun dateToMs_returnsEpochMillis() {
        val date = Date(1_700_000_000_000L)
        assertEquals(1_700_000_000_000L, converters.dateToMs(date))
    }

    @Test
    fun dateToMs_nullReturnsNull() {
        assertNull(converters.dateToMs(null))
    }

    @Test
    fun msToDate_returnsDate() {
        assertEquals(Date(1_700_000_000_000L), converters.msToDate(1_700_000_000_000L))
    }

    @Test
    fun msToDate_nullReturnsNull() {
        assertNull(converters.msToDate(null))
    }

    @Test
    fun tagsToString_joinsWithPipeDelimiter() {
        assertEquals("morning|with-food|before-bed", converters.tagsToString(listOf("morning", "with-food", "before-bed")))
    }

    @Test
    fun tagsToString_emptyListReturnsEmptyString() {
        assertEquals("", converters.tagsToString(emptyList()))
    }

    @Test
    fun tagsToString_singleTagHasNoDelimiter() {
        assertEquals("once", converters.tagsToString(listOf("once")))
    }

    @Test
    fun stringToTags_splitsOnPipe() {
        assertEquals(listOf("a", "b", "c"), converters.stringToTags("a|b|c"))
    }

    @Test
    fun stringToTags_emptyStringReturnsEmptyList() {
        assertEquals(emptyList<String>(), converters.stringToTags(""))
    }

    @Test
    fun roundTripPreservesTags() {
        val tags = listOf("morning", "with-food", "before-bed")
        assertEquals(tags, converters.stringToTags(converters.tagsToString(tags)))
    }

    @Test
    fun roundTripPreservesDate() {
        val date = Date(1_700_000_000_000L)
        val ms = converters.dateToMs(date)
        assertEquals(date, converters.msToDate(ms))
    }
}
