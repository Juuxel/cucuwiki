/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.config.postprocess

import com.electronwill.nightconfig.core.CommentedConfig
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

class Postprocessing {
    private val processors: MutableList<Entry<*>> = ArrayList()

    fun <A : Annotation> add(type: KClass<A>, processor: Postprocessor<A>) {
        processors += Entry(type, processor)
    }

    inline fun <reified A : Annotation> add(processor: Postprocessor<A>) =
        add(A::class, processor)

    fun apply(source: Any, config: CommentedConfig) {
        for (processor in processors) {
            processor.apply(source, config)
        }
    }

    private class Entry<A : Annotation>(private val type: KClass<A>, private val postprocessor: Postprocessor<A>) {
        private fun <T : Any> scan(value: T, path: List<String>, sink: (List<String>, A) -> Unit) {
            val valueType = value::class
            for (property in valueType.memberProperties) {
                val propertyPath = path + property.name

                @Suppress("UNCHECKED_CAST")
                val propertyValue = (property as KProperty1<T, *>).get(value)
                if (propertyValue != null) {
                    scan(propertyValue, propertyPath, sink)
                }

                val annotation = property.findAnnotations(type).singleOrNull()
                if (annotation != null) {
                    sink(propertyPath, annotation)
                }
            }
        }

        fun apply(source: Any, config: CommentedConfig) {
            val annotations = HashMap<List<String>, A>()
            scan(source, emptyList(), annotations::put)

            for ((path, annotation) in annotations) {
                postprocessor.apply(path, annotation, config)
            }
        }
    }
}
