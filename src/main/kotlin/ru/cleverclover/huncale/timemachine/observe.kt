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
package ru.cleverclover.huncale.timemachine

import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.temporal.ChronoUnit

internal data class Observatory(val scope: ObservationPeriod, val past: Alterable, val future: Alterable) {
    constructor() : this(ObservationPeriod(), Alterable(), Alterable())
}

internal data class ObservedDays(val before: Long, val after: Long) {
    constructor() : this(21, 180)
}

internal data class ObservationPeriod(val from: LocalDate, val to: LocalDate) {
    constructor() : this(
            LocalDate.now().minusDays(ObservedDays().before),
            LocalDate.now().plusDays(ObservedDays().after))

    fun years() = (from.year..to.year).asSequence()

    fun months(year: Int): Sequence<Int> {
        val start = if (from.year == year) from.month else Month.JANUARY
        val end = if (to.year > year) Month.DECEMBER else to.month
        return (start.value..end.value).asSequence()
    }

    fun monthStart(year: Int, month: Int) =
            if ((year == from.year) && (month == from.monthValue)) from.dayOfMonth
            else 1

    fun monthEnd(year: Int, month: Int) =
            if ((year == to.year) && (month == to.monthValue)) to.dayOfMonth
            else Month.of(month).length(Year.of(year).isLeap)

    fun length() = ChronoUnit.DAYS.between(from, to) + 1

    fun intersects(start: LocalDate, end: LocalDate) = (from <= end) && (to >= start)
}

internal data class Alterable(val wide: Boolean, val narrow: Boolean) {
    constructor() : this(true, true)
}