/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.config

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig
import com.electronwill.nightconfig.core.UnmodifiableConfig

fun Config.addDefaults(default: UnmodifiableConfig) {
    for ((key, value) in default.valueMap()) {
        if (key !in valueMap()) {
            valueMap()[key] = value
        } else if (value is UnmodifiableConfig) {
            val current = valueMap()[key]
            if (current is Config) {
                current.addDefaults(value)
            }
        }
    }

    if (this is CommentedConfig && default is UnmodifiableCommentedConfig) {
        for ((key, comment) in default.commentMap()) {
            if (key !in commentMap()) {
                commentMap()[key] = comment
            }
        }
    }
}
