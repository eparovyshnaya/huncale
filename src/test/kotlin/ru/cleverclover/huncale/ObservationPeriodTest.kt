/*******************************************************************************
 * Copyright (c) 2019, 2020 CleverClover
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT which is available at
 * https://spdx.org/licenses/MIT.html#licenseText
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *     CleverClover - initial API and implementation
 *******************************************************************************/
package ru.cleverclover.huncale

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import ru.cleverclover.huncale.timemachine.ObservationPeriod
import java.time.LocalDate
import java.time.Month
import kotlin.streams.asStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ObservationPeriodTest {

    @Test
    fun plainYears() {
        assertEquals(setOf(2019), plainPeriod().years().toSet())
    }

    @Test
    fun plainMonths() {
        assertEquals((Month.FEBRUARY.value..Month.APRIL.value).toSet(), plainPeriod().months(2019).toSet())
    }

    @Test
    fun wideYears() {
        assertEquals(setOf(2019, 2020), crossYearPeriod().years().toSet())
    }

    @Test
    fun wideMonth() {
        with(crossYearPeriod()) {
            assertEquals(setOf(Month.DECEMBER.value), months(2019).toSet())
            assertEquals((Month.JANUARY.value..Month.MARCH.value).toSet(), months(2020).toSet())
        }
    }

    @ParameterizedTest(name = "plain year period: {0}.{1} month bounds are [{2}, {3}]")
    @CsvSource(
        "2019, 2, 2, 28",
        "2019, 3, 1, 31",
        "2019, 4, 1, 12"
    )
    fun plainMonthBounds(year: Int, month: Int, start: Int, end: Int) {
        with(plainPeriod()) {
            assertEquals(monthStart(year, month), start)
            assertEquals(monthEnd(year, month), end)
        }
    }

    @ParameterizedTest(name = "cross year period: {0}.{1} month bounds are [{2}, {3}]")
    @CsvSource(
        "2019, 12, 2, 31",
        "2020, 1, 1, 31",
        "2020, 2, 1, 29",
        "2020, 3, 1, 1"
    )
    fun wideMonthBounds(year: Int, month: Int, start: Int, end: Int) {
        with(crossYearPeriod()) {
            assertEquals(monthStart(year, month), start)
            assertEquals(monthEnd(year, month), end)
        }
    }

    @Test
    fun length() {
        assertEquals(singleDayPeriod().length(), 1)
        assertEquals(plainPeriod().length(), 70)
        assertEquals(crossYearPeriod().length(), 91)
    }

    @ParameterizedTest
    @MethodSource("doNotIntersectWithPlainPeriod")
    fun plainDoesNotIntersect(another: Pair<LocalDate, LocalDate>) {
        assertFalse(plainPeriod().intersects(another.first, another.second))
    }

    @ParameterizedTest
    @MethodSource("intersectWithPlainPeriod")
    fun plainIntersects(another: Pair<LocalDate, LocalDate>) {
        assertTrue(plainPeriod().intersects(another.first, another.second))
    }

    @ParameterizedTest
    @MethodSource("doNotIntersectWithCrossYearPeriod")
    fun wideDoesNotIntersect(another: Pair<LocalDate, LocalDate>) {
        assertFalse(crossYearPeriod().intersects(another.first, another.second))
    }

    @ParameterizedTest
    @MethodSource("intersectWithCrossYearPeriod")
    fun wideIntersects(another: Pair<LocalDate, LocalDate>) {
        assertTrue(crossYearPeriod().intersects(another.first, another.second))
    }

    @Suppress("unused")
    fun doNotIntersectWithPlainPeriod() = doNotIntersect(plainPeriod())

    @Suppress("unused")
    fun intersectWithPlainPeriod() = intersect(plainPeriod())

    @Suppress("unused")
    fun doNotIntersectWithCrossYearPeriod() = doNotIntersect(crossYearPeriod())

    @Suppress("unused")
    fun intersectWithCrossYearPeriod() = intersect(crossYearPeriod())

    private fun doNotIntersect(period: ObservationPeriod) = with(period) {
        sequenceOf(
            Pair(from.minusMonths(1), from.minusDays(1)),
            Pair(to.plusDays(1), to.plusMonths(2)),
            Pair(from.plusYears(1).plusDays(1), to.plusYears(1).minusDays(1))
        ).asStream()
    }

    private fun intersect(period: ObservationPeriod) = with(period) {
        sequenceOf(
            Pair(from.minusMonths(1), from),
            Pair(from.minusMonths(1), from.plusDays(10)),
            Pair(from.minusDays(10), to.plusDays(10)),
            Pair(from.plusDays(1), to.minusDays(1)),
            Pair(from.plusDays(1), to.plusDays(1)),
            Pair(to, to.plusDays(10))
        )
            .asStream()
    }

    private fun singleDayPeriod() = ObservationPeriod(
        LocalDate.of(2019, Month.FEBRUARY, 1),
        LocalDate.of(2019, Month.FEBRUARY, 1)
    )

    private fun plainPeriod() = ObservationPeriod(
        LocalDate.of(2019, Month.FEBRUARY, 2),
        LocalDate.of(2019, Month.APRIL, 12)
    )

    private fun crossYearPeriod() = ObservationPeriod(
        LocalDate.of(2019, Month.DECEMBER, 2),
        LocalDate.of(2020, Month.MARCH, 1)
    )

}
