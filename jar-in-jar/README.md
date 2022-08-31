# jar-in-jar

A simple nested JAR extractor for Java 11.

## How does it work?

jar-in-jar is configured by creating a .properties file like this at `META-INF/jar-in-jar.properties`:

```properties
main-class = path.to.the.real.MainClass
jar-names = a.jar,b.jar
```

All the JARs listed in `jar-names` should be stored in `META-INF/jars`.

Finally, the final bundled JAR should be created with the classes from jar-in-jar
and the `META-INF` files mentioned above. The JAR manifest should have `Main-Class: juuxel.jarinjar.JarInJarLauncher`.

## Executing

When a bundled JAR is executed, it'll extract the nested JARs and add them to the classpath,
then execute the real main class.

The JARs are extracted into `./libraries` by default, but that can be changed with the `jarinjar.libraryPath` system
property.
