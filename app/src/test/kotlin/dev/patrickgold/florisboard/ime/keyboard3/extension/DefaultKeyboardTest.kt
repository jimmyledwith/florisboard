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

package dev.patrickgold.florisboard.ime.keyboard3.extension

import dev.patrickgold.florisboard.ime.core.Subtype
import dev.patrickgold.florisboard.ime.nlp.latin.LatinLanguageProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.io.File

class DefaultKeyboardTest : FunSpec({
    test("a fresh install loads English (US) QWERTY, not German QWERTZ") {
        DefaultFoundationKeyboardAsset shouldBe "keyboard/qwerty.xml"
        val keyboard = File(
            "src/main/assets/extensions/org.florisboard.k3.foundation/$DefaultFoundationKeyboardAsset",
        ).readText()
        keyboard shouldContain "locale=\"en-US\""
        keyboard shouldContain "layout=\"QWERTY\""
        keyboard shouldContain "<row keys=\"q w e r t y u i o p\" />"
        keyboard shouldContain "<row keys=\"base-to-shift z x c v b n m bksp\" />"
        keyboard shouldNotContain "<row keys=\"q w e r t z u i o p\" />"
        keyboard shouldNotContain "<row keys=\"base-to-shift y x c v b n m bksp\" />"
        // Corner hints follow FlorisBoard's English popup list, not the German umlaut/symbol set.
        keyboard shouldContain "longPressDefaultKeyId=\"a-ash\""
        keyboard shouldContain "longPressDefaultKeyId=\"e-charon\""
        keyboard shouldContain "longPressDefaultKeyId=\"i-grave\""
        keyboard shouldContain "longPressDefaultKeyId=\"o-tilde\""
        keyboard shouldContain "longPressDefaultKeyId=\"u-acute\""
        keyboard shouldNotContain "longPressDefaultKeyId=\"a-umlaut\""
        keyboard shouldNotContain "longPressDefaultKeyId=\"o-umlaut\""
        keyboard shouldNotContain "longPressDefaultKeyId=\"u-umlaut\""
        keyboard shouldNotContain "longPressDefaultKeyId=\"hash\""
        keyboard shouldNotContain "longPressDefaultKeyId=\"dollar\""
        keyboard shouldNotContain "longPressDefaultKeyId=\"percent\""
        keyboard shouldNotContain "longPressDefaultKeyId=\"amp\""
    }

    test("the fallback subtype is English (US) with the Latin suggestion provider") {
        Subtype.DEFAULT.primaryLocale.languageTag() shouldBe "en-US"
        Subtype.DEFAULT.nlpProviders.suggestion shouldBe LatinLanguageProvider.ProviderId
        Subtype.DEFAULT.nlpProviders.spelling shouldBe LatinLanguageProvider.ProviderId
    }
})
