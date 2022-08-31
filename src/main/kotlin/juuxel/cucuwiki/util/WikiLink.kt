package juuxel.cucuwiki.util

import juuxel.cucuwiki.Cucuwiki
import kotlin.io.path.notExists

class WikiLink(target: String, private val text: String) {
    private val target: String = PathNormalizer.normalizeAndSanitize(target)

    fun print(app: Cucuwiki): String = buildString {
        val red = app.repository.directory.resolve("$target.json").notExists()
        append("<a href=\"/wiki/$target\"")
        if (red) {
            append(" class=\"red-link\"")
        }
        append(">")
        appendEscapedHtmlText(text)
        append("</a>")
    }
}
