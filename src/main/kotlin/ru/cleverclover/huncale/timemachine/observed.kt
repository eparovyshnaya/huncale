package ru.cleverclover.huncale.timemachine

import ru.cleverclover.metacalendar.Cashed
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// output dto
internal object DateText {
    private val labelFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun label(date: LocalDate) = Cashed(date) { labelFormat.format(it) }
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

internal class Resources(){
    //fun data() =
}

