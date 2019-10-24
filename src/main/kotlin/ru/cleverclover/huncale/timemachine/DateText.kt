package ru.cleverclover.huncale.timemachine

import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

internal object DateText {
    private val labelFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun label(date: LocalDate): String = labelFormat.format(date)
    fun label(date: ZonedDateTime): String = labelFormat.format(date)
    fun date(label: String): LocalDate = with(labelFormat.parse(label)) {
        LocalDate.of(get(ChronoField.YEAR), get(ChronoField.MONTH_OF_YEAR), get(ChronoField.DAY_OF_MONTH))
    }
}