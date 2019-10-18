package ru.cleverclover.huncale.timemachine

import java.text.SimpleDateFormat

// input dto
internal class ObservatoryConfig(private val timeMachine: Map<String, Any>) {
    private val date = SimpleDateFormat("dd.MM.yyyy")
    fun observatory(): Observatory {
        // todo: implementation lc.itgroup.hunta.HuntingCalendarController#prepareObservationPeriod
        return Observatory() // todo: read timeMachine params: date-formatted {timestampStart, timestampEnd} and booleans {moveStart, moveEnd}
    }
}

