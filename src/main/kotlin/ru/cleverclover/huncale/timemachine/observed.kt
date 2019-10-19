package ru.cleverclover.huncale.timemachine

import ru.cleverclover.huncale.Restriction
import ru.cleverclover.huncale.Targets
import ru.cleverclover.huncale.Target
import ru.cleverclover.metacalendar.Cashed
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

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
            "name" to target.name(),
            "latinName" to target.latin(),
            "category" to target.category().name(),
            "categoryId" to target.category().id(),
            "restrictions" to target.restrictions().flatMap { restrictionData(it) }.toList())

    private fun restrictionData(restriction: Restriction) = scope.years()
            .flatMap { year ->
                restriction.period()
                        .resolve(year, ZoneId.systemDefault())
                        .map { Pair(it, restriction.condition()) }  // todo: UGLY!
                        .asSequence()
            }
            .toSet()
            .map {
                mapOf<String, Any>(
                        "sortKey" to it.first.first.toLocalDate(),
                        "condition" to "с ${DateText.label(it.first.first).get()} по ${DateText.label(it.first.second).get()} : ${it.second
                                ?: " Все половозрастные группы"}",
                        "limited" to (it.second != null),
                        "inScope" to scope.intersects(it.first.first.toLocalDate(), it.first.second.toLocalDate()))
            }
            .toList()


}

