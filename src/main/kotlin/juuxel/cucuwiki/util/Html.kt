package juuxel.cucuwiki.util

object Html {
    fun escapeText(str: String) = buildString(str.length) { appendEscapedHtmlText(str) }
}

fun StringBuilder.appendEscapedHtmlText(str: String) {
    for (c in str) {
        when (c) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            else -> append(c)
        }
    }
}
