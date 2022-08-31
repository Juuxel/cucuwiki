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

class FrontPage(private val app: Cucuwiki) : Endpoint {
    override fun handle(ctx: Context) {
        ctx.redirect("/wiki/${app.settings.content.frontPage}", HttpCode.SEE_OTHER.status)
    }
}
