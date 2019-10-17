package ru.cleverclover.huncale

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.cleverclover.huncale.timemachine.ObservationPeriod
import java.time.LocalDate
import java.time.Month

class ObservationPeriodTest {
    @Test
    fun singleYear() {
        val period = ObservationPeriod(LocalDate.of(2019, Month.FEBRUARY, 2),
                LocalDate.of(2019, Month.APRIL, 12))

        assertEquals(period.years().toSet(), setOf(2019))
        assertTrue(period.months(2019).toSet() == (Month.FEBRUARY.value..Month.APRIL.value).toSet())
        assert(period.monthStart(2019, Month.FEBRUARY.value) == 2)
        assert(period.monthEnd(2019, Month.FEBRUARY.value) == 28)
        assert(period.monthStart(2019, Month.MARCH.value) == 1)
        assert(period.monthEnd(2019, Month.MARCH.value) == 31)
        assert(period.monthStart(2019, Month.APRIL.value) == 1)
        assert(period.monthEnd(2019, Month.APRIL.value) == 12)
    }

    @Test
    fun crossYear() {
        val period = ObservationPeriod(LocalDate.of(2019, Month.FEBRUARY, 2),
                LocalDate.of(2020, Month.APRIL, 12))

        assertEquals(period.years().toSet(), setOf(2019, 2020))
        assertTrue(period.months(2019).toSet() == (Month.FEBRUARY.value..Month.DECEMBER.value).toSet())
        assertTrue(period.months(2020).toSet() == (Month.JANUARY.value..Month.APRIL.value).toSet())
        assert(period.monthStart(2019, Month.FEBRUARY.value) == 2)
        assert(period.monthEnd(2019, Month.FEBRUARY.value) == 28)
        assert(period.monthStart(2020, Month.FEBRUARY.value) == 1)
        assert(period.monthEnd(2020, Month.FEBRUARY.value) == 29)
        assert(period.monthStart(2020, Month.APRIL.value) == 1)
        assert(period.monthEnd(2020, Month.APRIL.value) == 12)
    }
}