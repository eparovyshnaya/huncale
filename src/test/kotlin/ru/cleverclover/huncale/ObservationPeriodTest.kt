package ru.cleverclover.huncale

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.cleverclover.huncale.timemachine.ObservationPeriod
import java.time.LocalDate
import java.time.Month

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
        assertEquals(plainPeriod().length(), 69)
        assertEquals(crossYearPeriod().length(), 90)
    }

    //@ParameterizedTest
    @CsvSource(
            "",
            "",
            ""
    )
    fun intersects(){

    }


    private fun plainPeriod() = ObservationPeriod(
            LocalDate.of(2019, Month.FEBRUARY, 2),
            LocalDate.of(2019, Month.APRIL, 12))


    private fun crossYearPeriod() = ObservationPeriod(
            LocalDate.of(2019, Month.DECEMBER, 2),
            LocalDate.of(2020, Month.MARCH, 1))

}