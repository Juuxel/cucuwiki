package juuxel.cucuwiki

import java.nio.file.Path

fun main() {
    // TODO: args
    val app = Cucuwiki(Path.of("."))
    app.launch()
}
