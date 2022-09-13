# cucuwiki

A simple wiki written in Kotlin and TypeScript.

Still very WIP, don't expect anything :)

## Building
Requirements:
- Java 11
- Node.js 17
- pnpm 7

To build, run `gradlew build` on the command line.

## Credits
- Server
  - [Javalin](https://javalin.io) for the HTTP code
  - [flexmark-java](https://github.com/vsch/flexmark-java/) for rendering Markdown
  - [Pebble](https://pebbletemplates.io/) for HTML templates
  - [SLF4J](https://slf4j.org) and [Logback](https://logback.qos.ch/) for logging
  - [JGit](https://www.eclipse.org/jgit/) for handling the wiki storage
  - [NightConfig](https://github.com/TheElectronWill/night-config/) for configuration
  - [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/) for handling JSON files
  - [jsoup](https://jsoup.org/) for parsing and cleaning HTML
- Client
  - [marked](https://marked.js.org/) for rendering Markdown
  - [DOMPurify](https://github.com/cure53/DOMPurify) for sanitising inputs for the preview
- Build tools
  - [Rollup](https://rollupjs.org) for bundling dependencies
  - [ESLint](https://eslint.org/) and [Prettier](https://prettier.io/) for checking and formatting code
  - [node-gradle](https://github.com/node-gradle/gradle-node-plugin/) for integrating NPM with the Gradle toolchain
