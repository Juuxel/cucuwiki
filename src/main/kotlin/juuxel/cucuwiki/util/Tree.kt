/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

sealed interface Tree<out E> {
    sealed interface WithValue<out E> : Tree<E> {
        val value: E
    }

    data class Root<out E>(
        val children: List<Tree<E>>
    ) : Tree<E>, Iterable<Tree<E>> by children

    data class Branch<out E>(
        override val value: E,
        val children: List<Tree<E>>
    ) : WithValue<E>, Iterable<Tree<E>> by children

    data class Leaf<out E>(
        override val value: E
    ) : WithValue<E>
}
