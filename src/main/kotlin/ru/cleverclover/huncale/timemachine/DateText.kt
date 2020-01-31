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
