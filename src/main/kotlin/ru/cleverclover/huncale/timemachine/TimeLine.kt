package ru.cleverclover.huncale.timemachine

import org.json.simple.JSONObject
import java.time.Month

internal class TimeLine(private val scope: ObservationPeriod) {
    fun yearsData() = JSONObject().apply {
        scope.years().forEach { this[it] = yearData(it) }
    }

    private fun yearData(year: Int) = JSONObject().apply {
        scope.months(year).forEach { this[it] = monthData(year, it) }
    }

    private fun monthData(year:Int, month:Int) = JSONObject().apply {
        put("maxInScope", scope.monthStart(year, month))
        put("minInScope", scope.monthEnd(year, month))
        put("name", Month.of(month).name)
    }
}