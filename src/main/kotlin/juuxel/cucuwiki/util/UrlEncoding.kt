/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

import java.net.URLEncoder

object UrlEncoding {
    /**
     * URL encodes a complete string.
     */
    fun encodeString(str: String) =
        URLEncoder.encode(str, Charsets.UTF_8)

    /**
     * URL encodes each component of a wiki path.
     */
    fun encodePath(path: String) =
        PathNormalizer.normalizeAndSanitize(path)
            .splitToSequence('/')
            .map { encodeString(it) }
            .joinToString(separator = "/")
}
