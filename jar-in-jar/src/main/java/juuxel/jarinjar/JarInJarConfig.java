package juuxel.jarinjar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

final class JarInJarConfig {
    final List<String> jarNames;
    final String mainClass;

    JarInJarConfig(List<String> jarNames, String mainClass) {
        this.jarNames = jarNames;
        this.mainClass = mainClass;
    }

    private static String getOrThrow(Properties properties, String name) {
        String value = properties.getProperty(name);
        if (value == null) throw new NoSuchElementException("Missing JAR-in-JAR property: " + name);
        return value;
    }

    static JarInJarConfig load() throws IOException {
        InputStream in = JarInJarConfig.class.getResourceAsStream("/META-INF/jar-in-jar.properties");
        if (in != null) {
            try (in; Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                Properties properties = new Properties();
                properties.load(reader);
                String mainClass = getOrThrow(properties, "main-class");
                List<String> jarNames = List.of(getOrThrow(properties, "jar-names").split(", *"));
                return new JarInJarConfig(jarNames, mainClass);
            }
        } else {
            throw new FileNotFoundException("/META-INF/jar-in-jar.properties");
        }
    }
}
