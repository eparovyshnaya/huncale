package ru.cleverclover.huncale

import org.json.simple.JSONArray
import org.json.simple.parser.JSONParser
import java.io.InputStreamReader

internal class JsonData {

    fun targets(): Targets {
        val categories: Categories = categories() ?: return NoTargets()
        return targets(categories) ?: return NoTargets()
    }

    private fun categories() =
            JsonData::class.java.classLoader.getResourceAsStream("data/category.json")?.use {
                Categories(JSONParser().parse(InputStreamReader(it)) as JSONArray)
            }

    private fun targets(categories: Categories) =
            JsonData::class.java.classLoader.getResourceAsStream("data/resource.json")?.use {
                Targets(JSONParser().parse(InputStreamReader(it)) as JSONArray, categories)
            }

}