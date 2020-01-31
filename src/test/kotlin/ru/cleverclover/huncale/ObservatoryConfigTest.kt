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
package ru.cleverclover.huncale

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.cleverclover.huncale.timemachine.Alterable
import ru.cleverclover.huncale.timemachine.DateText
import ru.cleverclover.huncale.timemachine.Observatory
import ru.cleverclover.huncale.timemachine.ObservatoryConfig
import java.time.LocalDate

class ObservatoryConfigTest {

    @Test
    @DisplayName("Default observatory allows all movements")
    fun default() =
        with(ObservatoryConfig(mapOf()).observatory()) {
            assertNavigationFits(Alterable(true, true), Alterable(true, true))
        }


    @Test
    @DisplayName("widest scope does not allow further widening")
    fun wide() {
        with(
            ObservatoryConfig(
                LocalDate.now().timeMachine(true, true, 100, 180)
            ).observatory()
        ) {
            assertNavigationFits(Alterable(false, true), Alterable(false, true))
        }
    }

    @Test
    @DisplayName("cannot narrow form left-envelop of 'now'")
    fun leftNow() {
        with(
            ObservatoryConfig(
                LocalDate.now().timeMachine(true, false, 30, 180)
            ).observatory()
        ) {
            assertNavigationFits(Alterable(true, false), Alterable(true, true))
        }
    }

    @Test
    @DisplayName("cannot narrow form right-envelop of 'now'")
    fun rightNow() =
        with(
            ObservatoryConfig(
                LocalDate.now().timeMachine(false, true, 180, 30)
            ).observatory()
        )
        {
            assertNavigationFits(Alterable(true, true), Alterable(true, false))
        }

    private fun LocalDate.timeMachine(forward: Boolean, backward: Boolean, minus: Long, plus: Long) =
        with(this) {
            mapOf(
                "moveStart" to forward.toString(),
                "moveBack" to backward.toString(),
                "timestampStart" to DateText.label(minusDays(minus)),
                "timestampEnd" to DateText.label(plusDays(plus))
            )
        }

    private fun Observatory.assertNavigationFits(past: Alterable, future: Alterable) {
        assertEquals(this.past, past)
        assertEquals(this.future, future)
        assertTrue(this.scope.from <= LocalDate.now().minusDays(3))
        assertTrue(this.scope.to >= LocalDate.now().plusDays(3))
    }

}
