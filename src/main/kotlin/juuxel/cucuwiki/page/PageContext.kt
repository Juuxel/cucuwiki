/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.util.UrlEncoding
import juuxel.cucuwiki.util.WikiLink
import kotlin.io.path.exists

class PageContext(private val app: Cucuwiki) {
    inline fun create(block: Dsl.() -> Unit): Map<String, Any> =
        Dsl().apply(block).asMap()

    inner class Dsl {
        private val map: MutableMap<String, Any> = HashMap()

        init {
            put("wikiName", app.settings.content.wikiName)
            put("frontPage", app.settings.content.frontPage)
        }

        fun asMap(): Map<String, Any> = map

        fun put(key: String, value: Any) {
            map[key] = value
        }

        fun currentContents(path: String) {
            val filePath = app.repository.resolveFile("$path.json") ?: return
            if (filePath.exists()) {
                val page = Page.load(filePath, app.charset) ?: return
                put("currentTitle", page.title)
                put("currentContent", page.content)
            }
        }

        fun pageTree() {
            put("pageTree", app.treeRenderer.getTree())
        }

        fun breadcrumbs(path: String) {
            put("breadcrumbs", breadcrumbify(path))
        }

        private fun breadcrumbify(path: String): String = buildString {
            val parts = path.split('/')
            for ((i, component) in parts.withIndex()) {
                append("<span class=\"breadcrumb-slash\"></span>")

                if (i == parts.lastIndex) {
                    append(component)
                } else {
                    val link = WikiLink(
                        target = parts.slice(0..i).joinToString(separator = "/"),
                        text = component
                    )
                    append(link.print(app))
                }
            }
        }

        fun articlePath(path: String) {
            put("articlePath", path)
            put("articlePathLink", UrlEncoding.encodePath(path))
        }
    }
}
