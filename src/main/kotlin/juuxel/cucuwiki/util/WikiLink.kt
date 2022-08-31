/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

import juuxel.cucuwiki.Cucuwiki
import kotlin.io.path.notExists

class WikiLink(target: String, private val text: String) {
    private val target: String = PathNormalizer.normalizeAndSanitize(target)

    fun print(app: Cucuwiki): String = buildString {
        val red = app.repository.directory.resolve("$target.json").notExists()
        append("<a href=\"/wiki/${UrlEncoding.encodePath(target)}\"")
        if (red) {
            append(" class=\"red-link\"")
        }
        append(">")
        appendEscapedHtmlText(text)
        append("</a>")
    }
}
