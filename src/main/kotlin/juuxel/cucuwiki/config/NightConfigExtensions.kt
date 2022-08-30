package juuxel.cucuwiki.config

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig
import com.electronwill.nightconfig.core.UnmodifiableConfig
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

fun <T : Any> addDefaultComments(value: T, config: CommentedConfig, path: List<String> = emptyList()) {
    val type = value::class
    for (property in type.memberProperties) {
        val propertyPath = path + property.name

        @Suppress("UNCHECKED_CAST")
        val propertyValue = (property as KProperty1<T, *>).get(value)
        if (propertyValue != null) {
            addDefaultComments(propertyValue, config, propertyPath)
        }

        if (config.getComment(propertyPath) == null) {
            val comments = property.findAnnotations(Comment::class)
            val comment = comments.singleOrNull() ?: continue
            config.setComment(propertyPath, comment.comment)
        }
    }
}

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
