package juuxel.jarinjar;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class JarInJarLauncher {
    public static void main(String[] args) throws IOException {
        Path extractedDir = Path.of(System.getProperty("jarinjar.libraryPath", "libraries"));
        Files.createDirectories(extractedDir);

        JarInJarConfig config = JarInJarConfig.load();
        List<URL> urls = new ArrayList<>();
        for (String jarName : config.jarNames) {
            InputStream res = JarInJarLauncher.class.getResourceAsStream("/META-INF/jars/" + jarName);
            if (res != null) {
                try (res) {
                    Path target = extractedDir.resolve(jarName);
                    Files.copy(res, target, StandardCopyOption.REPLACE_EXISTING);
                    urls.add(target.toUri().toURL());
                }
            }
        }

        try {
            URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]), JarInJarLauncher.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(cl);
            Class<?> main = Class.forName(config.mainClass, true, cl);
            Method method = main.getMethod("main", String[].class);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            lookup.unreflect(method).invokeExact(args);
        } catch (Throwable t) {
            throw new RuntimeException("Could not launch " + config.mainClass, t);
        }
    }
}
