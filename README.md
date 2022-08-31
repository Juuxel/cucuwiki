# cucuwiki

A simple wiki written in Kotlin and TypeScript.

Still very WIP, don't expect anything :)

## Credits
- Server
  - [Javalin](https://javalin.io) for the HTTP code
  - [flexmark-java](https://github.com/vsch/flexmark-java/) for rendering Markdown
  - [Pebble](https://pebbletemplates.io/) for HTML templates
  - [SLF4J](https://slf4j.org) and [Logback](https://logback.qos.ch/) for logging
  - [JGit](https://www.eclipse.org/jgit/) for handling the wiki storage
  - [NightConfig](https://github.com/TheElectronWill/night-config/) for configuration
  - [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/) for handling JSON files
- Client
  - [marked](https://marked.js.org/) for rendering Markdown
  - [DOMPurify](https://github.com/cure53/DOMPurify) and [html-escaper](https://github.com/WebReflection/html-escaper)
    for sanitising inputs for the preview
- Build tools
  - [Browserify](https://browserify.org/) and [Tinyify](https://github.com/browserify/tinyify) for bundling dependencies
  - [node-gradle](https://github.com/node-gradle/gradle-node-plugin/) for integrating NPM with the Gradle toolchain
