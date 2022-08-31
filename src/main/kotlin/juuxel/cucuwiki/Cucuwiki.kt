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
    val settings get() = configManager.settings
    val repository = WikiRepository(settings)
    val charset: Charset = try {
        Charset.forName(settings.storage.encoding)
    } catch (e: Exception) {
        LOGGER.error("Could not determine charset for encoding '{}', falling back to UTF-8", settings.storage.encoding)
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
        app.start(settings.networking.port)
        app.get("/") { ctx ->
            ctx.redirect("/wiki/${settings.content.frontPage}", HttpCode.SEE_OTHER.status)
        }
        app.get("/wiki/<name>") { ctx ->
            val action = ctx.queryParam("do")?.let(GetAction::byName) ?: GetAction.VIEW
            val name = ctx.pathParam("name")

            when (action) {
                GetAction.VIEW -> {
                    val path = repository.resolveFile("$name.json")

                    if (path == null) {
                        ctx.status(HttpCode.FORBIDDEN)
                        // TODO: Forbidden page
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

                GetAction.EDIT -> {
                    val html = pageRenderer.renderEdit(name)
                    ctx.html(html)
                }
            }
        }
        app.post("/wiki/<name>") { ctx ->
            val action = ctx.queryParam("do")?.let(PostAction::byName) ?: PostAction.UPDATE
            val name = ctx.pathParam("name")

            when (action) {
                PostAction.UPDATE -> {
                    val content = ctx.formParam("article_content")
                    if (content != null) {
                        val path = repository.resolveFile("$name.json")

                        if (path != null) {
                            val existing = if (path.exists()) {
                                Page.load(path, charset)
                            } else {
                                null
                            }

                            val page = Page(
                                ctx.formParam("title") ?: existing?.title ?: name,
                                content
                            )
                            page.save(path, charset)
                            repository.commitFile("$name.json", "root", null)
                            ctx.redirect("/wiki/$name?do=view", HttpCode.SEE_OTHER.status)
                        } else {
                            ctx.status(HttpCode.FORBIDDEN)
                            // TODO: Forbidden page
                            val page = pageRenderer.genericNotFound(name)
                            ctx.html(page)
                        }
                    } else {
                        ctx.status(HttpCode.BAD_REQUEST)
                        ctx.result("POST request missing article_content")
                    }
                }
            }
        }
    }

    companion object {
        private val LOGGER = logger()
    }
}
