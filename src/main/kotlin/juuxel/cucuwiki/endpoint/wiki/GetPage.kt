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
import kotlin.io.path.exists

class GetPage(private val app: Cucuwiki) : Endpoint {
    override fun handle(ctx: Context) {
        val action = ctx.queryParam("do")?.let(GetAction::byName) ?: GetAction.VIEW
        val name = PathNormalizer.normalizeAndSanitize(ctx.pathParam("name"))

        when (action) {
            GetAction.VIEW -> {
                val path = app.repository.resolveFile("$name.json")

                if (path == null) {
                    ctx.status(HttpCode.FORBIDDEN)
                    // TODO: Forbidden page
                    val page = app.pageRenderer.genericNotFound(name)
                    ctx.html(page)
                    return
                }

                val page = if (path.exists()) {
                    Page.load(path, app.charset)
                } else {
                    null
                }

                val html: String = if (page != null) {
                    app.pageRenderer.renderView(name, page)
                } else {
                    ctx.status(HttpCode.NOT_FOUND)
                    app.pageRenderer.articleNotFound(name)
                }
                ctx.html(html)
            }

            GetAction.EDIT -> {
                val html = app.pageRenderer.renderEdit(name)
                ctx.html(html)
            }
        }
    }
}
