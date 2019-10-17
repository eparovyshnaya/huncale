package ru.cleverclover.huncale.timemachine

import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.ZonedDateTime

internal data class Observatory(val scope: ObservationPeriod, val past: Alter, val future: Alter) {
    constructor() : this(ObservationPeriod(), Alter(), Alter())
}

internal data class ObservedDays(val before: Long, val after: Long) {
    constructor() : this(21, 90)
}

// todo: do smth with it
internal data class ObservationPeriod(val from: LocalDate, val to: LocalDate) {
    constructor() : this(LocalDate.now().minusDays(ObservedDays().before),
            LocalDate.now().plusDays(ObservedDays().after))

    internal fun years() = (from.year..to.year).asSequence()
    internal fun months(year: Int): Sequence<Int> {
        val start = if (from.year == year) from.month else Month.JANUARY
        val end = if (to.year > year) Month.DECEMBER else to.month
        return (start.value..end.value).asSequence()
    }

    internal fun monthStart(year: Int, month: Int) =
            if (year == from.year && month == from.monthValue) from.dayOfMonth
            else 1

    internal fun monthEnd(year: Int, month: Int) =
            if (year == to.year && month == to.monthValue) to.dayOfMonth
            else Month.of(month).length(Year.of(year).isLeap)
}

internal data class Alter(val wide: Boolean, val narrow: Boolean) {
    constructor() : this(true, true)
}