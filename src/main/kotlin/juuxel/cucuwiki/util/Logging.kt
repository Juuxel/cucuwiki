/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

/**
 * Gets a logger for the calling class.
 *
 * If called from a companion object, returns a logger
 * for the enclosing class.
 *
 * If called from a top-level property initialiser,
 * returns a logger for the enclosing file.
 */
fun logger(): Logger {
    @Suppress("UsePropertyAccessSyntax") // not a pure property
    var caller = STACK_WALKER.getCallerClass()
    val enclosing = caller.enclosingClass

    if (enclosing != null && caller.kotlin.isCompanion) {
        caller = enclosing
    }

    return LoggerFactory.getLogger(caller)
}
