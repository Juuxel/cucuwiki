/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.test

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import juuxel.cucuwiki.util.PathNormalizer.normalize
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PathNormalizerSpec : Spek({
    describe("normalization") {
        it("removes duplicate slashes") {
            expect(normalize("a///b//c/d"))
                .toEqual("a/b/c/d")
        }

        it("lower-cases strings") {
            expect(normalize("Apple/Banana/CucuWikI"))
                .toEqual("apple/banana/cucuwiki")
        }

        it("removes leading slashes") {
            expect(normalize("/home"))
                .toEqual("home")
        }

        it("removes trailing slashes") {
            expect(normalize("home/"))
                .toEqual("home")
        }

        it("normalizes Unicode characters") {
            expect(normalize("\u0061\u0308\u00E4"))
                .toEqual("\u00E4\u00E4")
        }
    }
})
