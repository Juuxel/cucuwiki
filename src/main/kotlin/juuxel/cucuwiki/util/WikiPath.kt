/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

class WikiPath(private val components: List<String>) {
    constructor(path: String) : this(PathNormalizer.normalizeAndSanitize(path).split('/'))

    init {
        require(components.isNotEmpty()) {
            "Path must have components!"
        }
    }

    fun startsWith(other: WikiPath): Boolean =
        this == other || (components.size >= other.components.size &&
            components.subList(0, other.components.size) == other.components.subList(0, other.components.size))

    override fun equals(other: Any?): Boolean =
        this === other || (other is WikiPath && components == other.components)

    override fun hashCode(): Int =
        components.hashCode()

    override fun toString(): String =
        PathNormalizer.normalizeAndSanitize(components.joinToString(separator = "/"))
}
