/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.config.postprocess

import com.electronwill.nightconfig.core.CommentedConfig

interface Postprocessor<in A : Annotation> {
    fun apply(path: List<String>, annotation: A, config: CommentedConfig)
}
