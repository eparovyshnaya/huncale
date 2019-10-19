package ru.cleverclover.huncale

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import ru.cleverclover.huncale.timemachine.Beacons
import ru.cleverclover.huncale.timemachine.ObservatoryConfig
import ru.cleverclover.huncale.timemachine.Resources
import ru.cleverclover.huncale.timemachine.TimeLine

@Controller
class AskMe {

    @GetMapping("/")
    fun dashboard(model: Model): String {
        model.addAttribute("targets", Data.targets())
        model.addAttribute("hi", "Hi there! We are up and going.")
        return "dashboard"
    }

    @GetMapping("/calendarDataAjax")
    @ResponseBody
    fun calendarData(@RequestParam params: Map<String, Any>) = with(ObservatoryConfig(params).observatory()) {
        mapOf("years" to TimeLine(scope).data(),
                "beacons" to Beacons(this).data(),
                "resources" to Resources(Data.targets(), scope).data()

        )
    }

}