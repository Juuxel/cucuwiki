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

class WikiLink {
    private val target: String
    private val text: String

    constructor(target: String, text: String) {
        this.target = PathNormalizer.normalizeAndSanitize(target)
        this.text = text
    }

    constructor(target: WikiPath, text: String) {
        this.target = target.toString()
        this.text = text
    }

    fun print(app: Cucuwiki, classes: List<String> = emptyList()): String = buildString {
        append("<a href=\"/wiki/${UrlEncoding.encodePath(target)}\" class=\"")
        for (className in classes) {
            append(' ')
            append(className)
        }
        if (isRedLink(app, target)) {
            append(" red-link")
        }
        append("\">")
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
                        if (target.startsWith("wiki/") && isRedLink(app, target.removePrefix("wiki/"))) {
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
