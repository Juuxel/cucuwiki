/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import juuxel.cucuwiki.util.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class Page(
    val title: String,
    val content: String,
) {
    fun save(path: Path, charset: Charset) {
        try {
            path.parent.createDirectories()
            val json = JSON.encodeToString(serializer(), this)
            path.writeText(json, charset)
        } catch (e: Exception) {
            LOGGER.error("Could not save page {} to path {} (charset: {})", this, path, charset)
        }
    }

    companion object {
        private val LOGGER = logger()
        private val JSON = Json { prettyPrint = true }

        fun load(path: Path, charset: Charset): Page? {
            if (path.notExists()) {
                LOGGER.error("Trying to load page from nonexistent path {}", path)
                return null
            }

            return try {
                val text = path.readText(charset)
                JSON.decodeFromString(serializer(), text)
            } catch (e: Exception) {
                LOGGER.error("Could not load page from path {} (charset: {})", path, charset, e)
                null
            }
        }
    }
}
