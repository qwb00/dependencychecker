package org.example.dependencychecker;

import org.objectweb.asm.*;

import java.util.HashSet;
import java.util.Set;

public class DependencyClassVisitor extends ClassVisitor {

    private final Set<String> dependencies;

    public DependencyClassVisitor() {
        super(Opcodes.ASM9);
        this.dependencies = new HashSet<>();
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public void visit(
            int version, int access, String name, String signature, String superName, String[] interfaces) {
        addInternalName(superName);
        addInternalNames(interfaces);
    }

    @Override
    public FieldVisitor visitField(
            int access, String name, String descriptor, String signature, Object value) {
        addDescriptor(descriptor);
        return null; // No need to visit further
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        addMethodDescriptor(descriptor);
        addInternalNames(exceptions);
        return new DependencyMethodVisitor(dependencies);
    }

    private void addDescriptor(String descriptor) {
        addType(Type.getType(descriptor));
    }

    private void addMethodDescriptor(String descriptor) {
        for (Type type : Type.getArgumentTypes(descriptor)) {
            addType(type);
        }
        addType(Type.getReturnType(descriptor));
    }

    private void addType(Type type) {
        if (type == null) return;

        switch (type.getSort()) {
            case Type.ARRAY:
                addType(type.getElementType());
                break;
            case Type.OBJECT:
                addInternalName(type.getInternalName());
                break;
            default:
                // Primitive types are ignored
                break;
        }
    }

    private void addInternalName(String internalName) {
        if (internalName == null) return;

        String className = internalName.replace('/', '.');
        dependencies.add(className);
    }

    private void addInternalNames(String[] internalNames) {
        if (internalNames == null) return;

        for (String name : internalNames) {
            addInternalName(name);
        }
    }
}
