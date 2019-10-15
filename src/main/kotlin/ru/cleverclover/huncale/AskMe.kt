package ru.cleverclover.huncale

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AskMe {

    @GetMapping("/")
    fun dashboard(model: Model): String {
        model.addAttribute("targets", JsonData().targets())
        model.addAttribute("hi", "Hi there! We are up and going.")
        return "dashboard"
    }
}