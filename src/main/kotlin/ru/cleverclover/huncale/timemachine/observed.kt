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

import ru.cleverclover.huncale.Target
import ru.cleverclover.huncale.Targets
import ru.cleverclover.metacalendar.Cashed
import ru.cleverclover.metacalendar.meta.MetaCalendar
import ru.cleverclover.metacalendar.resolve.NotedResolvedPeriod
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

// output dto
internal class TimeLine(private val scope: ObservationPeriod) {

    fun data() = scope.years().associateWith { yearData(it) }

    private fun yearData(year: Int) = scope.months(year).associateWith { monthData(year, it) }

    private fun monthData(year: Int, month: Int) = mapOf(
        "minInScope" to scope.monthStart(year, month),
        "maxInScope" to scope.monthEnd(year, month),
        "name" to Month.of(month).name
    )

}

internal class Beacons(private val observatory: Observatory) {

    fun data() = mapOf(
        "now" to now(),
        "start" to bound(observatory.scope.from, observatory.past),
        "end" to bound(observatory.scope.to, observatory.future),
        "distance" to observatory.scope.length(),
        "maxScope" to 300, // todo:constant somehow
        "moveStepLabel" to "28 дней"
    )

    private fun now() = with(LocalDate.now()) {
        mapOf(
            "offset" to ChronoUnit.DAYS.between(observatory.scope.from, this) + 1,
            "label" to DateText.label(this)
        )
    }

    private fun bound(date: LocalDate, alterable: Alterable) = mapOf(
        "label" to DateText.label(date),
        "canMoveToNow" to alterable.narrow,
        "canMoveFromNow" to alterable.wide
    )

}

internal class Resources(private val targets: Targets, private val scope: ObservationPeriod) {

    private val data = Cashed(targets) { targets.get().associateBy({ it.name() }, { targetData(it) }).toMap(TreeMap()) }
    fun data() = data.get()

    fun inScope() = data().count { (_, resource) -> resource["inScope"] as Boolean }

    private fun targetData(target: Target): Map<String, Any?> {
        val restrictionsData = restrictionsData(target)
        return mapOf(
            "id" to target.latin(),
            "name" to target.name(),
            "latinName" to target.latin(),
            "category" to target.category().name(),
            "categoryId" to target.category().id(),
            "restrictions" to restrictionsData,
            "inScope" to restrictionsData.any { it["inScope"] as Boolean }
        )
    }

    private fun restrictionsData(target: Target) =
        with(MetaCalendar(target.restrictions().map { it.period() })) {
            scope.years()
                .flatMap { resolve(it, ZoneId.systemDefault()).periods().asSequence() }
                .distinct()
                .sortedBy { it.from.toLocalDate() }
                .map { periodData(it) }
                .toList()
        }

    private fun periodData(period: NotedResolvedPeriod): Map<String, Any?> {
        val scoped = scope.intersects(period.from.toLocalDate(), period.to.toLocalDate())
        val start = Cashed(period) { Max(period.from.toLocalDate(), scope.from).get() }
        val end = Cashed(period) { Min(period.to.toLocalDate(), scope.to).get() }
        return mapOf(
            "condition" to "с ${DateText.label(period.from)} " +
                    "по ${DateText.label(period.to)} " +
                    ": ${period.note ?: " Все половозрастные группы"}",
            "limited" to (period.note != null),
            "inScope" to scoped, // todo: Refactor in synch with js. Or not.
            "startsAt" to if (scoped) ChronoUnit.DAYS.between(scope.from, start.get()) else null,
            "lasts" to if (scoped) ChronoUnit.DAYS.between(start.get(), end.get()) + 1 else null
        )
    }

}
