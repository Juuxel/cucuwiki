/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.util.Tree
import juuxel.cucuwiki.util.WikiLink
import juuxel.cucuwiki.util.WikiPath
import juuxel.cucuwiki.util.logger
import java.text.Collator
import java.util.Locale
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class PageTreeRenderer(private val app: Cucuwiki) {
    private var tree: TreeData? = null

    fun getTree(page: String): String {
        val tree = getTreeData()
        val path = WikiPath(page)

        return buildString {
            append("<div class=\"tree\">")
            for (branch in tree.branches) {
                appendBranch(branch, path)
            }
            for (leaf in tree.leaves) {
                appendLeaf(leaf, path)
            }
            append("</div>")
        }
    }

    private fun getTreeData(): TreeData = synchronized(this) {
        val current = tree
        if (current != null) return current
        val built = buildTreeData()
        tree = built
        built
    }

    private fun buildTreeData(): TreeData {
        val tree = PageTree.build(app) {
            val relative = app.repository.directory.relativize(it)
            val joined = relative.mapIndexed { index, name ->
                val base = name.fileName.toString()
                if (index == relative.nameCount - 1 && it.isRegularFile()) {
                    base.removeSuffix(".json")
                } else {
                    base
                }
            }.joinToString(separator = "/")

            val title = if (it.isDirectory()) {
                null
            } else {
                Page.load(it, app.charset)?.title
            }

            TreeEntry(WikiPath(joined), title ?: it.fileName.toString())
        }
        val collator = Collator.getInstance(Locale.ENGLISH) // TODO: locale
        val comparator: Comparator<Tree.WithValue<TreeEntry>> = Comparator.comparing({ it.value.title }, collator)
        val branches = tree.filterIsInstanceTo<Tree.Branch<TreeEntry>, _>(ArrayList())
        branches.sortWith(comparator)
        val leaves = tree.filterIsInstanceTo<Tree.Leaf<TreeEntry>, _>(ArrayList())
        leaves.sortWith(comparator)
        return TreeData(branches, leaves)
    }

    fun invalidate() {
        synchronized(this) {
            tree = null
        }
    }

    private fun StringBuilder.appendBranch(branch: Tree.Branch<TreeEntry>, currentPage: WikiPath) {
        append("<details class=\"tree-branch\"")
        if (currentPage.startsWith(branch.value.path)) {
            append(" open=\"\"")
        }
        append("><summary>")
        if (branch.value.path == currentPage) {
            append("<span class=\"current-page\">")
            append(branch.value.title)
            append("</span>")
        } else {
            append(WikiLink(branch.value.path, branch.value.title).print(app))
        }
        append("</summary><div class=\"tree-branch-children\">")
        for (child in branch) {
            when (child) {
                is Tree.Branch -> {
                    appendBranch(child, currentPage)
                }
                is Tree.Leaf -> {
                    appendLeaf(child, currentPage)
                }
                is Tree.Root -> LOGGER.warn("Tree.Root found as child in {}", branch)
            }
        }
        append("</div></details>")
    }

    private fun StringBuilder.appendLeaf(leaf: Tree.Leaf<TreeEntry>, currentPage: WikiPath) {
        if (leaf.value.path == currentPage) {
            append("<span class=\"current-page tree-leaf\">")
            append(leaf.value.title)
            append("</span>")
        } else {
            append(WikiLink(leaf.value.path, leaf.value.title).print(app, classes = listOf("tree-leaf")))
        }
    }

    companion object {
        private val LOGGER = logger()
    }

    private class TreeEntry(val path: WikiPath, val title: String)

    private class TreeData(val branches: List<Tree.Branch<TreeEntry>>, val leaves: List<Tree.Leaf<TreeEntry>>)
}
