package ru.cleverclover.huncale

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import ru.cleverclover.metacalendar.Cashed
import ru.cleverclover.metacalendar.PeriodFromRangeDefinition
import ru.cleverclover.metacalendar.period
import java.io.InputStreamReader

internal object Data {

    fun targets(): Targets {
        val categories: Categories = categories() ?: return NoTargets()
        return targets(categories) ?: return NoTargets()
    }

    private fun categories() =
            Data::class.java.classLoader.getResourceAsStream("data/category.json")?.use {
                Categories(JSONParser().parse(InputStreamReader(it)) as JSONArray)
            }

    private fun targets(categories: Categories) =
            Data::class.java.classLoader.getResourceAsStream("data/resource.json")?.use {
                Targets(JSONParser().parse(InputStreamReader(it)) as JSONArray, categories)
            }
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

internal open class Targets(source: JSONArray, val categories: Categories) {
    private val targets = Cashed(source) { array ->
        array.map {
            Target(it as JSONObject) { obj -> categories.byId(obj["category"] as String)!! }
        }
    }

    fun get() = targets.get()
}

internal class NoTargets : Targets(JSONArray(), Categories(JSONArray()))

internal class Target(private val source: JSONObject, private val categoryFromSource: (source: JSONObject) -> Category) {
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

    fun conditional() = source.containsKey("conditions")

}