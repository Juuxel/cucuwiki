/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki

import io.javalin.Javalin
import io.javalin.http.HttpCode
import juuxel.cucuwiki.config.ConfigManager
import juuxel.cucuwiki.endpoint.Endpoints
import juuxel.cucuwiki.endpoint.wiki.GetAction
import juuxel.cucuwiki.endpoint.wiki.PostAction
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

        app.start(settings.networking.port)
        Endpoints(this).apply(app)
    }

    companion object {
        private val LOGGER = logger()
    }
}
