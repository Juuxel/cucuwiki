package juuxel.cucuwiki.plugin

interface Plugin {
    // List of URLs or file paths
    val scripts: List<String> get() = listOf()
}
