/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import juuxel.cucuwiki.Cucuwiki
import java.io.StringWriter

class PageRenderer(private val app: Cucuwiki) {
    private val context = PageContext(app)
    private val engine: PebbleEngine = PebbleEngine.Builder()
        .loader(ClasspathLoader().also {
            it.prefix = "templates"
        })
        .build()

    private fun render(templateName: String, context: Map<String, Any>): String {
        val template = engine.getTemplate("$templateName.peb.html")
        val writer = StringWriter()
        template.evaluate(writer, context)
        return writer.toString()
    }

    fun renderView(path: String, page: Page): String =
        render(
            "view",
            context.create {
                pageTree()
                breadcrumbs(path)
                put("title", page.title)
                put("markdown", Markdown.render(page.content))
                articlePath(path)
            }
        )

    fun renderEdit(path: String): String =
        render(
            "edit",
            context.create {
                pageTree()
                breadcrumbs(path)
                currentContents(path)
                put("title", "Editing /$path")
                articlePath(path)
            }
        )

    fun genericNotFound(message: String): String =
        render(
            "404",
            context.create {
                put("message", message)
            }
        )

    fun articleNotFound(path: String): String =
        render(
            "missingarticle",
            context.create {
                pageTree()
                breadcrumbs(path)
                articlePath(path)
            }
        )
}
