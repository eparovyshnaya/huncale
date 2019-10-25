package ru.cleverclover.huncale

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.cleverclover.huncale.timemachine.DateText
import ru.cleverclover.huncale.timemachine.ObservationPeriod
import ru.cleverclover.huncale.timemachine.ObservatoryConfig
import java.time.LocalDate

class ObservatoryConfigTest {
    @Test
    @DisplayName("Default observatory allows all movements")
    fun default() {
        with(ObservatoryConfig(mapOf()).observatory()) {
            assertTrue(past.narrow)
            assertTrue(past.wide)
            assertTrue(future.narrow)
            assertTrue(future.wide)
            assertContainsNow(scope)
        }
    }

    @Test
    @DisplayName("widest scope does not allow further widening")
    fun wide() {
        val timeMachine = with(LocalDate.now()) {
            mapOf(
                    "moveStart" to "true",
                    "moveBack" to "true",
                    "timestampStart" to DateText.label(minusDays(100)),
                    "timestampEnd" to DateText.label(plusDays(180)))
        }
        with(ObservatoryConfig(timeMachine).observatory()) {
            assertTrue(past.narrow)
            assertFalse(past.wide)
            assertTrue(future.narrow)
            assertFalse(future.wide)
            assertContainsNow(scope)
        }
    }

    @Test
    @DisplayName("cannot narrow form left-envelop of 'now'")
    fun leftNow() {
        val timeMachine = with(LocalDate.now()) {
            mapOf(
                    "moveStart" to "true",
                    "moveBack" to "false",
                    "timestampStart" to DateText.label(minusDays(30)),
                    "timestampEnd" to DateText.label(plusDays(180)))
        }
        with(ObservatoryConfig(timeMachine).observatory()) {
            assertFalse(past.narrow)
            assertTrue(past.wide)
            assertTrue(future.narrow)
            assertTrue(future.wide)
            assertContainsNow(scope)
        }
    }

    @Test
    @DisplayName("cannot narrow form right-envelop of 'now'")
    fun rightNow() {
        val timeMachine = with(LocalDate.now()) {
            mapOf(
                    "moveStart" to "false",
                    "moveBack" to "true",
                    "timestampStart" to DateText.label(minusDays(180)),
                    "timestampEnd" to DateText.label(plusDays(30)))
        }
        with(ObservatoryConfig(timeMachine).observatory()) {
            assertTrue(past.narrow)
            assertTrue(past.wide)
            assertFalse(future.narrow)
            assertTrue(future.wide)
            assertContainsNow(scope)
        }
    }

    private fun assertContainsNow(period: ObservationPeriod) {
        assertTrue(period.from <= LocalDate.now().minusDays(3))
        assertTrue(period.to >= LocalDate.now().plusDays(3))
    }
}