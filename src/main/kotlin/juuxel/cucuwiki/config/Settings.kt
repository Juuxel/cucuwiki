/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.cucuwiki.config

import com.electronwill.nightconfig.core.conversion.ForceBreakdown

class Settings {
    @Comment("Settings related to networking.")
    @field:ForceBreakdown // we need this for attaching the comments
    var networking: Networking = Networking()

    @Comment("Settings related to data storage and files.")
    @field:ForceBreakdown
    var storage: Storage = Storage()

    @Comment("Settings related to wiki content.")
    @field:ForceBreakdown
    var content: Content = Content()

    class Networking {
        @Comment("The port number that the server listens to.")
        var port: Int = 7770
    }

    class Storage {
        @Comment("The file path to the Git repository.")
        var repositoryPath: String = "wiki.git"

        @Comment("The character encoding to use for reading and saving text files.")
        var encoding: String = "UTF-8"
    }

    class Content {
        @Comment("The name of the wiki.")
        var wikiName = "My Cucuwiki"

        @Comment("The path to the front page.")
        var frontPage = "home"
    }
}
