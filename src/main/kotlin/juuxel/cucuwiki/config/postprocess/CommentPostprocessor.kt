/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.config.postprocess

import com.electronwill.nightconfig.core.CommentedConfig

object CommentPostprocessor : Postprocessor<Comment> {
    override fun apply(path: List<String>, annotation: Comment, config: CommentedConfig) {
        if (config.contains(path) && !config.containsComment(path)) {
            config.setComment(path, annotation.comment)
        }
    }
}
