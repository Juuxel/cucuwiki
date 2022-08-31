/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.endpoint

import io.javalin.Javalin
import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.endpoint.wiki.FrontPage
import juuxel.cucuwiki.endpoint.wiki.GetPage
import juuxel.cucuwiki.endpoint.wiki.PostPage

class Endpoints(app: Cucuwiki) {
    private val get: Map<String, Endpoint> = mapOf(
        "/" to FrontPage(app),
        "/wiki" to FrontPage(app),
        "/wiki/<name>" to GetPage(app),
    )
    private val post: Map<String, Endpoint> = mapOf(
        "/wiki/<name>" to PostPage(app),
    )

    fun apply(javalin: Javalin) {
        for ((path, endpoint) in get) {
            javalin.get(path, EndpointHandler(endpoint))
        }

        for ((path, endpoint) in post) {
            javalin.post(path, EndpointHandler(endpoint))
        }
    }
}
