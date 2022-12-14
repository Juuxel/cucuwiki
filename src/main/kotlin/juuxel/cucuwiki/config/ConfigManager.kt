/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.config

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig
import com.electronwill.nightconfig.core.conversion.ObjectConverter
import com.electronwill.nightconfig.core.file.CommentedFileConfig
import com.electronwill.nightconfig.core.io.WritingMode
import juuxel.cucuwiki.config.postprocess.CommentPostprocessor
import juuxel.cucuwiki.config.postprocess.NormalizePathPostprocessor
import juuxel.cucuwiki.config.postprocess.Postprocessing
import juuxel.cucuwiki.util.logger
import java.nio.file.Path

class ConfigManager(runDirectory: Path) {
    private val defaultConfig: UnmodifiableCommentedConfig by lazy {
        val default = Settings()
        val config = CommentedConfig.inMemory()
        ObjectConverter().toConfig(default, config)
        val postprocessing = Postprocessing()
        postprocessing.add(CommentPostprocessor)
        postprocessing.apply(default, config)
        config
    }

    val settings = try {
        load(runDirectory)
    } catch (e: Exception) {
        throw RuntimeException("Failed to load config", e)
    }

    private fun load(runDirectory: Path): Settings {
        LOGGER.info("Loading config from {}", FILE_NAME)
        val fileConfig = CommentedFileConfig.builder(runDirectory.resolve(FILE_NAME))
            .charset(Charsets.UTF_8)
            .onFileNotFound { file, configFormat ->
                LOGGER.info("Config file not found, writing out default config")
                configFormat.createWriter().write(defaultConfig, file, WritingMode.REPLACE, Charsets.UTF_8)
                true
            }
            .build()
        fileConfig.load()

        fileConfig.addDefaults(defaultConfig)

        val result = Settings()
        ObjectConverter().toObject(fileConfig, result)
        val postprocessing = Postprocessing()
        postprocessing.add(NormalizePathPostprocessor)
        postprocessing.apply(result, fileConfig)

        // Save with possible new settings
        fileConfig.save()

        return result
    }

    companion object {
        private const val FILE_NAME = "cucuwiki.toml"
        private val LOGGER = logger()
    }
}