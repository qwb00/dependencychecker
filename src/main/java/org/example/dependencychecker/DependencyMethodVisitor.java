package org.example.dependencychecker;

import org.objectweb.asm.*;

import java.util.Set;

public class DependencyMethodVisitor extends MethodVisitor {

    private final Set<String> dependencies;

    public DependencyMethodVisitor(Set<String> dependencies) {
        super(Opcodes.ASM9);
        this.dependencies = dependencies;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        addType(Type.getObjectType(type));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        addType(Type.getObjectType(owner));
        addDescriptor(descriptor);
    }

    @Override
    public void visitMethodInsn(
            int opcode, String owner, String name, String descriptor, boolean isInterface) {
        addType(Type.getObjectType(owner));
        addMethodDescriptor(descriptor);
    }

    @Override
    public void visitInvokeDynamicInsn(
            String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        addMethodDescriptor(descriptor);
        addHandle(bootstrapMethodHandle);
        for (Object arg : bootstrapMethodArguments) {
            if (arg instanceof Handle) {
                addHandle((Handle) arg);
            } else if (arg instanceof Type) {
                addType((Type) arg);
            }
        }
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (value instanceof Type) {
            addType((Type) value);
        }
    }

    private void addHandle(Handle handle) {
        addType(Type.getObjectType(handle.getOwner()));
        addMethodDescriptor(handle.getDesc());
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
                dependencies.add(type.getClassName());
                break;
            default:
                // Primitive types are ignored
                break;
        }
    }
}
