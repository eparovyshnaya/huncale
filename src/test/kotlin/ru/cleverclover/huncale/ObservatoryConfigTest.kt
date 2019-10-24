package ru.cleverclover.huncale

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.cleverclover.huncale.timemachine.ObservatoryConfig
import java.time.LocalDate

class ObservatoryConfigTest {
    @Test
    @DisplayName("Default observatory allows all movements and contains 'now'")
    fun default() {
        with(ObservatoryConfig(mapOf()).observatory()) {
            assertTrue(past.narrow)
            assertTrue(past.wide)
            assertTrue(future.narrow)
            assertTrue(future.wide)
            assertTrue(scope.intersects(LocalDate.now().minusDays(1), LocalDate.now()))
        }
    }


}