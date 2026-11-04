/*
 * Copyright (C) 2026 The FlorisBoard Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.ime.nlp.latin

import dev.patrickgold.florisboard.ime.keyboard3.completionEdit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.k3lp.runtime.K3Content
import org.k3lp.runtime.K3SurroundingText
import org.k3lp.runtime.K3TextRange
import java.io.File

class LatinSuggestionTest : FunSpec({
    val words = mapOf(
        "the" to 255,
        "that" to 254,
        "they" to 254,
        "apple" to 255,
        "don't" to 200,
        "don" to 100,
    )

    test("prefix matches are ranked by frequency") {
        suggestionsForPrefix(words, "th", 3).map { it.text } shouldContainExactly listOf("the", "that", "they")
        suggestionsForPrefix(words, "th", 3).first().confidence shouldBe 1.0
    }

    test("typed capitalization is kept") {
        suggestionsForPrefix(words, "Th", 1).map { it.text } shouldContainExactly listOf("The")
        suggestionsForPrefix(words, "TH", 1).map { it.text } shouldContainExactly listOf("THE")
    }

    test("empty input and non-words produce no suggestions") {
        suggestionsForPrefix(words, "", 8) shouldBe emptyList()
        suggestionsForPrefix(words, "th ", 8) shouldBe emptyList()
        suggestionsForPrefix(words, "th", 0) shouldBe emptyList()
    }

    test("apostrophe stays part of the token") {
        trailingWord("say don't") shouldBe "don't"
        suggestionsForPrefix(words, "don", 2).map { it.text } shouldContainExactly listOf("don't", "don")
    }

    test("composition text is preferred, otherwise the tail of text before the cursor") {
        val composed = K3Content(
            selection = K3TextRange(10),
            composition = K3TextRange(8, 10),
            surroundingText = K3SurroundingText(textBefore = "th"),
        )
        typedToken(composed) shouldBe "th"

        val plain = K3Content(
            selection = K3TextRange(6),
            surroundingText = K3SurroundingText(textBefore = "say th"),
        )
        typedToken(plain) shouldBe "th"
    }

    test("accepting a suggestion replaces the partial word") {
        val plain = K3Content(
            selection = K3TextRange(6),
            surroundingText = K3SurroundingText(textBefore = "say th"),
        )
        val edit = completionEdit(plain, "the")
        edit!!.rangeStart shouldBe 4
        edit.rangeEnd shouldBe 6
        edit.newTextBefore shouldBe "say the"
        edit.newCursor shouldBe 7

        val composed = K3Content(
            selection = K3TextRange(10),
            composition = K3TextRange(8, 10),
            surroundingText = K3SurroundingText(textBefore = "th"),
        )
        val composedEdit = completionEdit(composed, "that")
        composedEdit!!.newTextBefore shouldBe "that"
        composedEdit.rangeStart shouldBe 8
        composedEdit.rangeEnd shouldBe 10
        composedEdit.newCursor shouldBe 12
    }

    test("the bundled English list suggests real words for th") {
        val dictionary = File("src/main/assets/ime/dict/data.json")
        val parsed = Json.decodeFromString(
            MapSerializer(String.serializer(), Int.serializer()),
            dictionary.readText(),
        )
        suggestionsForPrefix(parsed, "th", 4).map { it.text } shouldContainExactly listOf(
            "the", "that", "they", "this",
        )
    }
})
