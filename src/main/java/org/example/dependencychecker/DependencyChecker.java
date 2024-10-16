package org.example.dependencychecker;

import org.objectweb.asm.ClassReader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class DependencyChecker {

    private final ClassLoader classLoader;
    private final Set<String> visitedClasses;
    private final Set<String> missingClasses;

    public DependencyChecker(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.visitedClasses = new HashSet<>();
        this.missingClasses = new HashSet<>();
    }

    public Set<String> findMissingDependencies(String mainClassName) {
        processClass(mainClassName);
        return missingClasses;
    }

    private void processClass(String className) {
        if (className == null || visitedClasses.contains(className)) {
            return;
        }
        visitedClasses.add(className);

        // Ignore Java core classes
        if (isJavaCoreClass(className)) {
            return;
        }

        String classResourceName = className.replace('.', '/') + ".class";
        try (InputStream classInputStream = classLoader.getResourceAsStream(classResourceName)) {
            if (classInputStream == null) {
                missingClasses.add(className);
                return;
            }

            ClassReader classReader = new ClassReader(classInputStream);
            DependencyClassVisitor classVisitor = new DependencyClassVisitor();
            classReader.accept(classVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            classVisitor.getDependencies().forEach(this::processClass);
        } catch (Exception e) {
            System.err.println("Failed to process class: " + className);
            e.printStackTrace();
        }
    }

    private boolean isJavaCoreClass(String className) {
        return className.startsWith("java.")
                || className.startsWith("javax.")
                || className.startsWith("sun.")
                || className.startsWith("com.sun.")
                || className.startsWith("jdk.")
                || className.startsWith("org.w3c.")
                || className.startsWith("org.xml.")
                || className.startsWith("org.omg.")
                || className.startsWith("org.ietf.")
                || className.startsWith("org.jcp.")
                || className.startsWith("org.relaxng.");
    }
}
