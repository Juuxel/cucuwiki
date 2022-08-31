/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

object Html {
    fun escapeText(str: String) = buildString(str.length) { appendEscapedHtmlText(str) }
}

fun StringBuilder.appendEscapedHtmlText(str: String) {
    for (c in str) {
        when (c) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            else -> append(c)
        }
    }
}
