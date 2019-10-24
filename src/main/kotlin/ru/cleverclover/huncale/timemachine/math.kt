package ru.cleverclover.huncale.timemachine

// todo find a place for it
internal class Max<T : Comparable<T>>(private val first: T, private val second: T) {
    fun get() = if (first >= second) first else second
}

internal class Min<T : Comparable<T>>(private val first: T, private val second: T) {
    fun get() = if (first <= second) first else second
}
