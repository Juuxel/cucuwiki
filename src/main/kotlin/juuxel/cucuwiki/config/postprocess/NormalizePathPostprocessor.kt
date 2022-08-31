/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.config.postprocess

import com.electronwill.nightconfig.core.CommentedConfig
import juuxel.cucuwiki.util.PathNormalizer

object NormalizePathPostprocessor : Postprocessor<NormalizePath> {
    override fun apply(path: List<String>, annotation: NormalizePath, config: CommentedConfig) {
        if (config.contains(path)) {
            val existing = config.get<String>(path)
            val normalized = PathNormalizer.normalizeAndSanitize(existing)
            config.set<Any?>(path, normalized)
        }
    }
}
