package juuxel.cucuwiki.page

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import juuxel.cucuwiki.Cucuwiki
import java.io.StringWriter

class PageRenderer(private val app: Cucuwiki) {
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

    private fun breadcrumbify(path: String): String = buildString {
        for (component in path.split('/')) {
            append("<span class=\"breadcrumb-slash\"></span>")
            append(component)
        }
    }

    private fun context(vararg pairs: Pair<String, Any>): Map<String, Any> = buildMap {
        put("wikiName", app.settings.content.wikiName)
        put("frontPage", app.settings.content.frontPage)

        for ((key, value) in pairs) {
            put(key, value)
        }
    }

    fun renderView(path: String, page: Page): String =
        render(
            "view",
            context(
                "breadcrumbs" to breadcrumbify(path),
                "title" to page.title,
                "markdown" to renderMarkdown(page.content),
            )
        )

    fun renderEdit(path: String): String =
        render(
            "edit",
            context(
                "breadcrumbs" to breadcrumbify(path),
                "title" to "Editing /$path",
                "articlePath" to path,
            )
        )

    fun genericNotFound(message: String): String =
        render(
            "404",
            context(
                "message" to message,
            )
        )

    fun articleNotFound(path: String): String =
        render(
            "missingarticle",
            context(
                "breadcrumbs" to breadcrumbify(path),
                "articlePath" to path,
            )
        )
}
