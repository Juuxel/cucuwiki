/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

import java.lang.StringBuilder
import java.text.Normalizer
import java.util.stream.Collectors

object PathNormalizer {
    private val BANNED_CHARACTERS = charArrayOf('.', '\\', ':', '@', '<', '>', '|', '?', '*', '"').map { it.code }

    private fun isBannedCharacter(codePoint: Int): Boolean =
        codePoint in BANNED_CHARACTERS || Character.isISOControl(codePoint)

    fun normalize(path: String): String {
        val withoutSlashes = StringBuilder().apply {
            for (codePoint in path.codePoints()) {
                if (codePoint == '/'.code && (isEmpty() || last() == '/')) {
                    continue // skip leading and duplicate slashes
                } else {
                    append(Character.toString(Character.toLowerCase(codePoint)))
                }
            }
        }

        return Normalizer.normalize(withoutSlashes, Normalizer.Form.NFC).removeSuffix("/")
    }

    fun check(path: String): String {
        path.codePoints().forEach { codePoint ->
            if (isBannedCharacter(codePoint)) {
                throw PathException("Banned character '${Character.toString(codePoint)}' in '$path'")
            }
        }

        return path
    }

    fun normalizeAndSanitize(path: String) =
        normalize(path).codePoints()
            .filter { !isBannedCharacter(it) }
            .mapToObj { Character.toString(it) }
            .collect(Collectors.joining())

    class PathException(message: String) : IllegalArgumentException(message)
}
