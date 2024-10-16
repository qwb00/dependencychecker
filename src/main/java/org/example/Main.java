package org.example;

import org.example.dependencychecker.DependencyChecker;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar dependencychecker.jar <main class> <jar files...>");
            System.exit(1);
        }

        String mainClassName = args[0];
        String[] jarFiles = Arrays.copyOfRange(args, 1, args.length);

        try {
            URL[] jarURLs = Arrays.stream(jarFiles)
                    .map(path -> {
                        try {
                            return new File(path).toURI().toURL();
                        } catch (Exception e) {
                            throw new RuntimeException("Invalid JAR file path: " + path, e);
                        }
                    })
                    .toArray(URL[]::new);

            URLClassLoader classLoader = new URLClassLoader(jarURLs);

            DependencyChecker checker = new DependencyChecker(classLoader);
            Set<String> missingClasses = checker.findMissingDependencies(mainClassName);

            if (missingClasses.isEmpty()) {
                System.out.println("All dependencies are satisfied.");
            } else {
                System.out.println("Missing dependencies:");
                missingClasses.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while checking dependencies:");
            e.printStackTrace();
        }
    }
}