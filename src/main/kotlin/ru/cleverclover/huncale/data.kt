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

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import ru.cleverclover.metacalendar.Cashed
import ru.cleverclover.metacalendar.parse.PeriodFromRangeDefinition
import ru.cleverclover.metacalendar.parse.period
import java.io.InputStreamReader

internal object Data {

    private val categories = Cashed("data/category.json") { source ->
        Data::class.java.classLoader.getResourceAsStream(source).use {
            Categories(JSONParser().parse(InputStreamReader(it!!, "UTF-8")) as JSONArray)
        }
    }
    private val targets = Cashed("data/resource.json") { source ->
        Data::class.java.classLoader.getResourceAsStream(source).use {
            Targets(JSONParser().parse(InputStreamReader(it!!, "UTF-8")) as JSONArray, categories())
        }
    }

    fun targets() = targets.get()
    fun categories() = categories.get()

}

internal class Categories(source: JSONArray) {

    private val categories = Cashed(source) { array -> array.map { Category(it as JSONObject) } }

    fun byId(id: String) = categories.get().find { it.id() == id }
    fun all() = categories.get()

}

internal class Category(private val source: JSONObject) {
    fun id() = source["id"]
    fun name() = source["name"]

}

internal open class Targets(source: JSONArray, categories: Categories) {

    private val targets = Cashed(source) { array ->
        array.map {
            Target(it as JSONObject) { obj -> categories.byId(obj["category"] as String)!! }
        }
    }

    fun get() = targets.get()

}

internal class NoTargets : Targets(JSONArray(), Categories(JSONArray()))

internal class Target(
    private val source: JSONObject,
    private val categoryFromSource: (source: JSONObject) -> Category
) {

    private val restrictions = Cashed(source["restrictions"] as JSONArray)
    { array -> array.map { Restriction(it as JSONObject) } }

    fun name() = source["name"]
    fun latin() = source["latinName"]
    fun hasRestriction() = source.containsKey("restrictions")
    fun restrictions() = restrictions.get()
    fun category() = categoryFromSource(source)

}

internal class Restriction(private val source: JSONObject) {

    private val period = Cashed(source) { obj ->
        PeriodFromRangeDefinition(obj["period"] as String)
            .bounds()
            .period(source["conditions"])
    }

    fun period() = period.get()

}
