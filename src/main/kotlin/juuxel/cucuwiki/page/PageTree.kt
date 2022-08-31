/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.util.Tree
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.isDirectory

object PageTree {
    fun <T> build(app: Cucuwiki, valueExtractor: (Path) -> T): Tree.Root<T> =
        Tree.Root(
            build(
                app.repository.directory,
                valueExtractor,
                root = true
            )
        )

    private fun <T> build(directory: Path, valueExtractor: (Path) -> T, root: Boolean): List<Tree<T>> {
        val directories = HashMap<String, Path>()
        val pages = HashMap<String, Path>()

        Files.list(directory).use { children ->
            children.filter {
                !root || it.fileName.toString() != ".git"
            }.forEach {
                val fileName = it.fileName.toString()
                if (it.isDirectory()) {
                    directories[fileName] = it
                } else if (it.toString().endsWith(".json")) {
                    val key = fileName.substring(0, fileName.length - ".json".length)
                    pages[key] = it
                }
            }
        }

        val result = ArrayList<Tree<T>>()

        for ((name, path) in directories) {
            val children = build(directory.resolve(path), valueExtractor, root = false)
            result.add(
                if (name in pages) {
                    Tree.Branch(valueExtractor(pages[name]!!), children)
                } else {
                    Tree.Branch(valueExtractor(path), children)
                }
            )
        }

        for ((name, path) in pages) {
            if (name !in directories) {
                result += Tree.Leaf(valueExtractor(path))
            }
        }

        return result
    }
}
