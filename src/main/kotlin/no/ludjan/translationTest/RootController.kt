package no.ludjan.translationTest

import no.ludjan.translationTest.translation.ExtractForTranslation
import no.ludjan.translationTest.translation.TranslationLookuper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController() {

    @ExtractForTranslation(
        "greeting",
        "A phrase for greeting someone, like 'Howdy partner!'",
        "Yo, zup?",
        "en",
    )
    lateinit var greeting: String

    @ExtractForTranslation(
        "farewell",
        "A phrase for saying goodbye to someone, like 'See ya later, aligator-skater'",
        "Bye bye bye",
        "en",
    )
    lateinit var farewell: String

    @GetMapping("get-translation")
    fun getEnWord(): ResponseEntity<*> {
        return ResponseEntity.ok("$greeting, and $farewell")
    }

}