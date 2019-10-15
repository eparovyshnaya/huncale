package ru.cleverclover.huncale

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RunMe

fun main(args: Array<String>) {
    runApplication<RunMe>(*args)
}
