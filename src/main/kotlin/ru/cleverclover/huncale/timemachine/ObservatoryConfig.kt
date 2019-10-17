package ru.cleverclover.huncale.timemachine

import org.omg.CORBA.Object

// dto ->
internal class ObservatoryConfig(private val timeMachine: Map<String, Any>) {
    fun observatory() = Observatory() // todo: read params
}