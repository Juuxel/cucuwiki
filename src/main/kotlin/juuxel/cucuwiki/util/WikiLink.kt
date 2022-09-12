/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

import juuxel.cucuwiki.Cucuwiki
import org.jsoup.nodes.Document
import java.net.URI
import java.net.URISyntaxException
import kotlin.io.path.notExists

class WikiLink(target: String, private val text: String) {
    private val target: String = PathNormalizer.normalizeAndSanitize(target)

    fun print(app: Cucuwiki): String = buildString {
        append("<a href=\"/wiki/${UrlEncoding.encodePath(target)}\"")
        if (isRedLink(app, target)) {
            append(" class=\"red-link\"")
        }
        append(">")
        appendEscapedHtmlText(text)
        append("</a>")
    }

    companion object {
        fun isRedLink(app: Cucuwiki, link: String): Boolean =
            app.repository.directory.resolve("$link.json").notExists()

        fun modifyLinks(app: Cucuwiki, doc: Document) {
            for (anchor in doc.select("a")) {
                val ref = anchor.attr("href")
                if (ref.isEmpty()) continue

                try {
                    val uri = URI(ref)
                    if (uri.isAbsolute) {
                        anchor.addClass("leaving-link")
                    } else {
                        val target = PathNormalizer.normalizeAndSanitize(ref)
                        if (isRedLink(app, target)) {
                            anchor.addClass("red-link")
                        }
                    }
                } catch (e: URISyntaxException) {
                    // just ignore it, we don't need to deal with it
                }
            }
        }
    }
}
