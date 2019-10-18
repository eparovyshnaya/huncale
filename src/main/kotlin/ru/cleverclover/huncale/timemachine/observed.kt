package ru.cleverclover.huncale.timemachine

import org.json.simple.JSONObject
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
    fun data() = JSONObject().apply {
        scope.years().forEach { this[it] = yearData(it) }
    }

    private fun yearData(year: Int) = JSONObject().apply {
        scope.months(year).forEach { this[it] = monthData(year, it) }
    }

    private fun monthData(year: Int, month: Int) = JSONObject().apply {
        put("minInScope", scope.monthStart(year, month))
        put("maxInScope", scope.monthEnd(year, month))
        put("name", Month.of(month).name)
    }
}

internal class Beacons(private val observatory: Observatory) {
    fun data() = JSONObject().apply {
        put("now", now())
        put("start", bound(observatory.scope.from, observatory.past))
        put("end", bound(observatory.scope.to, observatory.future))
        put("distance", observatory.scope.length())
        put("maxScope", 300) // todo:constant somehow
        put("moveStepLabel", "28 дней")  //lc.itgroup.hunta.HuntingCalendarController#composeBeacons
    }

    private fun now() = JSONObject().apply {
        val now = LocalDate.now()
        put("offset", ChronoUnit.DAYS.between(observatory.scope.from, LocalDate.now()) + 1)
        put("label", DateText.label(now).get())
    }

    private fun bound(date: LocalDate, alter: Alter) = JSONObject().apply {
        put("label", DateText.label(date).get())
        put("canMoveToNow", alter.narrow)
        put("canMoveFromNow", alter.wide)
    }
}

