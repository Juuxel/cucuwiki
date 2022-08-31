/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.page

import juuxel.cucuwiki.Cucuwiki
import juuxel.cucuwiki.util.Tree
import juuxel.cucuwiki.util.logger
import java.text.Collator
import java.util.Locale
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class PageTreeRenderer(private val app: Cucuwiki) {
    private var value: String? = null

    fun getTree(): String = synchronized(this) {
        val current = value
        if (current != null) return current
        val rendered = render()
        value = rendered
        rendered
    }

    private fun render(): String {
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

            TreeEntry(joined, title ?: it.fileName.toString())
        }
        val collator = Collator.getInstance(Locale.ENGLISH) // TODO: locale
        val comparator: Comparator<Tree.WithValue<TreeEntry>> = Comparator.comparing({ it.value.title }, collator)
        val branches = tree.filterIsInstanceTo<Tree.Branch<TreeEntry>, _>(ArrayList())
        branches.sortWith(comparator)
        val leaves = tree.filterIsInstanceTo<Tree.Leaf<TreeEntry>, _>(ArrayList())
        leaves.sortWith(comparator)

        return buildString {
            append("<div class=\"tree\">")
            for (branch in branches) {
                appendBranch(branch)
            }
            for (leaf in leaves) {
                appendLeaf(leaf)
            }
            append("</div>")
        }
    }

    fun invalidate() {
        synchronized(this) {
            value = null
        }
    }

    private fun StringBuilder.appendEscaped(str: String) {
        for (c in str) {
            when (c) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                else -> append(c)
            }
        }
    }

    private fun StringBuilder.appendBranch(branch: Tree.Branch<TreeEntry>) {
        append("<div class=\"titled-border\"><div class=\"border-title\"><a href=\"/wiki/${branch.value.path}\">")
        appendEscaped(branch.value.title)
        append("</a></div><div class=\"border-contents tree\">")
        for (child in branch) {
            when (child) {
                is Tree.Branch -> {
                    appendBranch(child)
                }
                is Tree.Leaf -> {
                    appendLeaf(child)
                }
                is Tree.Root -> LOGGER.warn("Tree.Root found as child in {}", branch)
            }
        }
        append("</div></div>")
    }

    private fun StringBuilder.appendLeaf(leaf: Tree.Leaf<TreeEntry>) {
        append("<a href=\"/wiki/${leaf.value.path}\">")
        appendEscaped(leaf.value.title)
        append("</a>")
    }

    companion object {
        private val LOGGER = logger()
    }

    private class TreeEntry(val path: String, val title: String)
}
