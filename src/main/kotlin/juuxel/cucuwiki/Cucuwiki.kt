package juuxel.cucuwiki

import io.javalin.Javalin
import io.javalin.http.HttpCode
import juuxel.cucuwiki.config.ConfigManager
import juuxel.cucuwiki.git.WikiRepository
import juuxel.cucuwiki.page.Page
import juuxel.cucuwiki.page.PageRenderer
import juuxel.cucuwiki.util.logger
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.exists

class Cucuwiki(val runDirectory: Path) {
    private val configManager = ConfigManager(runDirectory)
    val config get() = configManager.config
    val repository = WikiRepository(config)
    val charset: Charset = try {
        Charset.forName(config.storage.encoding)
    } catch (e: Exception) {
        LOGGER.error("Could not determine charset for encoding '{}', falling back to UTF-8", config.storage.encoding)
        Charsets.UTF_8
    }
    val pageRenderer = PageRenderer(this)

    fun launch() {
        val app = Javalin.create { config ->
            config.addStaticFiles {
                it.directory = "/static"
                it.hostedPath = "/static"
            }
        }

        // TODO: Handle internal server error
        app.start(config.networking.port)
        app.get("/") { ctx ->
            ctx.redirect("wiki/${config.content.frontPage}", HttpCode.SEE_OTHER.status)
        }
        app.get("/wiki/<name>") { ctx ->
            val action = ctx.queryParam("do")?.let(PageAction::byName) ?: PageAction.VIEW
            val name = ctx.pathParam("name")

            when (action) {
                PageAction.VIEW -> {
                    val path = repository.resolveFile("$name.json")

                    if (path == null) {
                        ctx.status(HttpCode.NOT_FOUND)
                        val page = pageRenderer.genericNotFound(name)
                        ctx.html(page)
                        return@get
                    }

                    val page = if (path.exists()) {
                        Page.load(path, charset)
                    } else {
                        null
                    }

                    val html: String = if (page != null) {
                        pageRenderer.renderView(name, page)
                    } else {
                        ctx.status(HttpCode.NOT_FOUND)
                        pageRenderer.articleNotFound(name)
                    }
                    ctx.html(html)
                }

                PageAction.EDIT -> {
                    val html = pageRenderer.renderEdit(name)
                    ctx.html(html)
                }
            }
        }
    }

    companion object {
        private val LOGGER = logger()
    }
}
