package ru.cleverclover.huncale.timemachine

import ru.cleverclover.huncale.Targets
import ru.cleverclover.huncale.Target
import ru.cleverclover.metacalendar.Cashed
import ru.cleverclover.metacalendar.MetaCalendar
import ru.cleverclover.metacalendar.NotedResolvedPeriod
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Collections.max

// output dto
internal object DateText {
    private val labelFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun label(date: LocalDate) = Cashed(date) { labelFormat.format(it) }
    fun label(date: ZonedDateTime) = Cashed(date) { labelFormat.format(it) }
}

internal class TimeLine(private val scope: ObservationPeriod) {
    fun data() = scope.years().associateWith { yearData(it) }

    private fun yearData(year: Int) = scope.months(year).associateWith { monthData(year, it) }

    private fun monthData(year: Int, month: Int) = mapOf(
            "minInScope" to scope.monthStart(year, month),
            "maxInScope" to scope.monthEnd(year, month),
            "name" to Month.of(month).name)
}

internal class Beacons(private val observatory: Observatory) {
    fun data() = mapOf(
            "now" to now(),
            "start" to bound(observatory.scope.from, observatory.past),
            "end" to bound(observatory.scope.to, observatory.future),
            "distance" to observatory.scope.length(),
            "maxScope" to 300, // todo:constant somehow
            "moveStepLabel" to "28 дней")  //lc.itgroup.hunta.HuntingCalendarController#composeBeacons


    private fun now() = with(LocalDate.now()) {
        mapOf("offset" to ChronoUnit.DAYS.between(observatory.scope.from, this) + 1,
                "label" to DateText.label(this).get())
    }

    private fun bound(date: LocalDate, alter: Alter) = mapOf(
            "label" to DateText.label(date).get(),
            "canMoveToNow" to alter.narrow,
            "canMoveFromNow" to alter.wide)
}

internal class Resources(private val targets: Targets, private val scope: ObservationPeriod) {
    fun data() = targets.get().associateBy({ it.name() }, { targetData(it) }).toMap(TreeMap())

    private fun targetData(target: Target) = mapOf(
            "id" to target.latin(),
            "name" to target.name(),
            "latinName" to target.latin(),
            "category" to target.category().name(),
            "categoryId" to target.category().id(),
            "restrictions" to restrictionsData(target),
            "inScope" to true) // todo

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
                "condition" to "с ${DateText.label(period.from).get()} " +
                        "по ${DateText.label(period.to).get()} " +
                        ": ${period.note ?: " Все половозрастные группы"}",
                "limited" to (period.note != null),
                "inScope" to scoped, // todo:
                "startsAt" to if (scoped) ChronoUnit.DAYS.between(scope.from, start.get()) else null,
                "lasts" to if (scoped) ChronoUnit.DAYS.between(start.get(), end.get()) + 1 else null
        )
    }
}


// todo find a place for it
internal class Max<T : Comparable<T>>(private val first: T, private val second: T) {
    fun get() = if (first >= second) first else second
}

internal class Min<T : Comparable<T>>(private val first: T, private val second: T) {
    fun get() = if (first <= second) first else second
}

