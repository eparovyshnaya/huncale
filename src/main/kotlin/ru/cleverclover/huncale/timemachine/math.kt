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

// todo find a place for it
internal class Max<T : Comparable<T>>(private val first: T, private val second: T) {
    fun get() = if (first >= second) first else second
}

internal class Min<T : Comparable<T>>(private val first: T, private val second: T) {
    fun get() = if (first <= second) first else second
}
