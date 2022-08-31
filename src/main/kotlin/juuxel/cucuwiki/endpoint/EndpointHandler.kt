/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.endpoint

import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.HttpCode

class EndpointHandler(private val endpoint: Endpoint) : Handler {
    override fun handle(ctx: Context) {
        try {
            endpoint.handle(ctx)
        } catch (e: Exception) {
            // TODO: Internal server error page
            ctx.status(HttpCode.INTERNAL_SERVER_ERROR)
            ctx.result("Internal server error, oh no")
        }
    }
}