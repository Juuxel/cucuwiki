/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.util.WikiLink
import org.jsoup.Jsoup
import org.jsoup.safety.Cleaner
import org.jsoup.safety.Safelist

object Markdown {
    private val SAFELIST = Safelist.relaxed()
        .preserveRelativeLinks(true)
        .addAttributes(":all", "class")
    private val parser: Parser
    private val renderer: HtmlRenderer

    init {
        val options = MutableDataSet()
        parser = Parser.builder(options).build()
        renderer = HtmlRenderer.builder(options)
            .build()
    }

    private fun getBaseUrl(app: Cucuwiki): String {
        val url = app.settings.networking.baseUrl
        if (url.isNotEmpty()) return url

        return "http://localhost:${app.settings.networking.port}/"
    }

    fun render(app: Cucuwiki, markdown: String, wikiLinks: Boolean = true): String {
        val rendered = renderer.render(parser.parse(markdown))
        return if (wikiLinks) {
            val doc = Jsoup.parseBodyFragment(rendered, getBaseUrl(app))
            val clean = Cleaner(SAFELIST).clean(doc)
            WikiLink.modifyLinks(app, clean)
            clean.body().html()
        } else {
            Jsoup.clean(rendered, getBaseUrl(app), SAFELIST)
        }
    }
}