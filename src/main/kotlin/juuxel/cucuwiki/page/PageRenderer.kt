/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import juuxel.cucuwiki.Cucuwiki
import java.io.StringWriter
import kotlin.io.path.exists

class PageRenderer(private val app: Cucuwiki) {
    private val context = PageContext(app)
    private val engine: PebbleEngine = PebbleEngine.Builder()
        .loader(ClasspathLoader().also {
            it.prefix = "templates"
        })
        .build()
    private val parser: Parser
    private val renderer: HtmlRenderer

    init {
        val options = MutableDataSet()
        parser = Parser.builder(options).build()
        renderer = HtmlRenderer.builder(options)
            .escapeHtml(true)
            .build()
    }

    private fun renderMarkdown(markdown: String): String =
        renderer.render(parser.parse(markdown))

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
                put("markdown", renderMarkdown(page.content))
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
