/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.git

import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.util.logger
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.nio.file.Path

class WikiRepository(private val app: Cucuwiki) {
    val directory: Path
    private val gitRepo: Repository
    private val git: Git

    init {
        val basePath = Path.of(app.settings.storage.repositoryPath)
        directory = if (basePath.isAbsolute) {
            basePath
        } else {
            app.runDirectory.resolve(basePath).toAbsolutePath()
        }

        val gitDir = directory.resolve(".git")

        gitRepo = FileRepositoryBuilder()
            .setWorkTree(directory.toFile())
            .setGitDir(gitDir.toFile())
            .build()

        LOGGER.info("Setting up Git repository at {}", directory.toAbsolutePath())

        if (gitRepo.objectDatabase.exists()) {
            LOGGER.info("Found existing repository")
        } else {
            LOGGER.info("Creating new repository")
            gitRepo.create()
        }

        git = Git(gitRepo)
    }

    // TODO: Test
    fun resolveFile(path: String): Path? {
        val result = directory.resolve(path).toAbsolutePath()

        if (!result.startsWith(directory)) {
            LOGGER.error("Tried to access off-limits file {}", result)
            return null
        }

        return result
    }

    fun commitFile(path: String, author: String, message: String?) {
        LOGGER.info("Committing {} (author: {}, message: {})", path, author, message)
        try {
            val add = git.add()
            add.addFilepattern(path)
            add.call()

            val commit = git.commit()
            commit.message = buildString {
                append("Update $path")
                append("\n\n")
                append("Author: ")
                append(author)

                if (message != null) {
                    append("Edit-Message: ")
                    append(message)
                }
            }
            commit.setAuthor(author, "$author@cucuwiki")
            commit.call()
            app.treeRenderer.invalidate()
        } catch (e: GitAPIException) {
            LOGGER.error("Could not commit! (path: {}, author: {}, message: {})", path, author, message, e)
        }
    }

    companion object {
        private val LOGGER = logger()
    }
}
