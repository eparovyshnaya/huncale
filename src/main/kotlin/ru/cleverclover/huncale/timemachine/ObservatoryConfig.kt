package ru.cleverclover.huncale.timemachine

import ru.cleverclover.metacalendar.Cashed
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// input dto
internal class ObservatoryConfig(private val timeMachine: Map<String, Any>) {
    private val step = 28L
    private val envelope = Cashed(3L) { with(LocalDate.now()) { Pair(minusDays(it), plusDays(it)) } }
    private val direction = Cashed(null) {
        val leftBeacon = (timeMachine["moveStart"] as String).toBoolean()
        val direction = if ((timeMachine["moveBack"] as String).toBoolean()) -1 else 1
        Pair(
                if (leftBeacon) direction else 0,
                if (leftBeacon) 0 else direction)
    }

    fun observatory(): Observatory {
        return if (timeMachine.isEmpty()) Observatory()
        else period().align().cut().let {
            Observatory(it,
                    Alterable(canWide(it), canNarrowLeft(it)),
                    Alterable(canWide(it), canNarrowRight(it)))
        }
    }

    private fun period() = ObservationPeriod(
            DateText.date(timeMachine["timestampStart"] as String),
            DateText.date(timeMachine["timestampEnd"] as String))

    private fun ObservationPeriod.align() = with(direction.get()) {
        ObservationPeriod(from.align(first), to.align(second))
    }

    private fun LocalDate.align(direction: Int) = plusDays(direction * step)

    private fun ObservationPeriod.cut() = ObservationPeriod(from.cutLeft(), to.cutRight())

    private fun LocalDate.cutLeft() = Min(this, envelope.get().first).get()

    private fun LocalDate.cutRight() = Max(this, envelope.get().second).get()

    private fun canWide(period: ObservationPeriod) =
            ChronoUnit.DAYS.between(period.from, period.to) + step <= 300

    private fun canNarrowRight(period: ObservationPeriod) = period.to > envelope.get().second

    private fun canNarrowLeft(period: ObservationPeriod) = period.from < envelope.get().first
}