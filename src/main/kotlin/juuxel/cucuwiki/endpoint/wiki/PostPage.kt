/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.endpoint.wiki

import io.javalin.http.Context
import io.javalin.http.HttpCode
import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.endpoint.Endpoint
import juuxel.cucuwiki.page.Page
import juuxel.cucuwiki.util.PathNormalizer
import juuxel.cucuwiki.util.UrlEncoding
import java.net.URLEncoder
import kotlin.io.path.exists

class PostPage(private val app: Cucuwiki) : Endpoint {
    override fun handle(ctx: Context) {
        val action = ctx.queryParam("do")?.let(PostAction::byName) ?: PostAction.UPDATE
        val name = PathNormalizer.normalizeAndSanitize(ctx.pathParam("name"))

        when (action) {
            PostAction.UPDATE -> {
                val content = ctx.formParam("article_content")
                if (content != null) {
                    val path = app.repository.resolveFile("$name.json")

                    if (path != null) {
                        val existing = if (path.exists()) {
                            Page.load(path, app.charset)
                        } else {
                            null
                        }

                        var title = ctx.formParam("title") ?: existing?.title ?: name
                        if (title.isEmpty()) title = name

                        val page = Page(title, content)
                        page.save(path, app.charset)
                        app.repository.commitFile("$name.json", "root", null)
                        ctx.redirect(
                            "/wiki/${UrlEncoding.encodePath(name)}?do=view",
                            HttpCode.SEE_OTHER.status
                        )
                    } else {
                        ctx.status(HttpCode.FORBIDDEN)
                        // TODO: Forbidden page
                        val page = app.pageRenderer.genericNotFound(name)
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
