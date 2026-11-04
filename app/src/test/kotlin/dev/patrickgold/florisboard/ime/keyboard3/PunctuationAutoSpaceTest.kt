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

package dev.patrickgold.florisboard.ime.keyboard3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PunctuationAutoSpaceTest : FunSpec({
    test("period and semicolon gain one space after a word") {
        punctuationAutoSpace(".", "Hello", "", enabled = true) shouldBe PunctuationAutoSpace(". ", true)
        punctuationAutoSpace(";", "Hello", "", enabled = true) shouldBe PunctuationAutoSpace("; ", true)
    }

    test("letters keep the space; digits and more punctuation retract it") {
        shouldRetractPunctuationAutoSpace("W") shouldBe false
        shouldRetractPunctuationAutoSpace("5") shouldBe true
        shouldRetractPunctuationAutoSpace(".") shouldBe true
        shouldRetractPunctuationAutoSpace("/") shouldBe true
        shouldRetractPunctuationAutoSpace(" ") shouldBe true
        shouldRetractPunctuationAutoSpace("the") shouldBe false
    }

    test("numbers, urls, emails, abbreviations, and an existing space are left alone") {
        punctuationAutoSpace(".", "3", "", enabled = true).insertedSpace shouldBe false
        punctuationAutoSpace(".", "www", "", enabled = true).insertedSpace shouldBe false
        punctuationAutoSpace(".", "https://example", "", enabled = true).insertedSpace shouldBe false
        punctuationAutoSpace(".", "name@mail", "", enabled = true).insertedSpace shouldBe false
        punctuationAutoSpace(".", "e", "", enabled = true).insertedSpace shouldBe false
        punctuationAutoSpace(".", "Hello", " ", enabled = true).insertedSpace shouldBe false
        punctuationAutoSpace(".", "Hello", "", enabled = false).insertedSpace shouldBe false
        punctuationAutoSpace("a", "Hello", "", enabled = true).insertedSpace shouldBe false
    }
})
