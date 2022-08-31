/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki

import java.nio.file.Path

fun main() {
    // TODO: args
    val app = Cucuwiki(Path.of("."))
    app.launch()
}
