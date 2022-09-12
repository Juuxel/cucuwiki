package juuxel.cucuwiki.page

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

object Markdown {
    private val parser: Parser
    private val renderer: HtmlRenderer

    init {
        val options = MutableDataSet()
        parser = Parser.builder(options).build()
        renderer = HtmlRenderer.builder(options)
            .build()
    }

    fun render(markdown: String): String {
        val rendered = renderer.render(parser.parse(markdown))
        return Jsoup.clean(rendered, Safelist.relaxed())
    }
}