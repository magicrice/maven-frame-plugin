package com.magicrice;


import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ModifyOriginalClass implements Opcodes {
    public static void main(String[] args) {
        File root = new File("/home/zhangkaiyue/workspace/report_server/target");
        insert(root);
//        File root = new File("/home/zhangkaiyue/workspace/report_server/target/classes/com/taijihuabao/reportplugin/service/impl/ClientBusinessServiceImpl.class");
//        insert(root);
    }

    public static void insert(File root) {
        if (root.isDirectory()) {
            for (File file : root.listFiles()) {
                insert(file);
            }
        }
        if (root.getName().endsWith(".class")) {
            try {
                doInsert(root);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public static void doInsert(File file) throws IOException {
        ClassReader cr = new ClassReader(new FileInputStream(file));
        ClassWriter cw = new ClassWriter(cr, ClassReader.EXPAND_FRAMES);
        cr.accept(new ClassVisitor(ASM5, cw) {
            private String className = "";
            private boolean flag = false;

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                System.out.println("执行的类为：" + file.getPath());
                super.visit(version, access, name, signature, superName, interfaces);
                this.className = name;
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if ("Lorg/springframework/stereotype/Service;".equals(descriptor)) {
                    flag = true;
                }
                return super.visitAnnotation(descriptor, visible);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
                if ("<init>".equals(name)) {
                    return methodVisitor;
                }
                if (!flag) {
                    return methodVisitor;
                }
                if(name.contains("lambda")){
                    //lambda
                    return methodVisitor;
                }
                if("<clinit>".equals(name)){
                    //静态方法区
                    return methodVisitor;
                }
                return new MyMethodVisitor(api, methodVisitor, access, name, descriptor, className);
            }

        }, ClassReader.EXPAND_FRAMES);
        byte[] bytes = cw.toByteArray();
//        String path = "/home/zhangkaiyue/Test.class";
        System.out.println("输出路径：" + file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        }


    }

    static class MyMethodVisitor extends AdviceAdapter {
        private String className;
        private int soltIndex;

        protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String className) {
            super(api, methodVisitor, access, name, descriptor);
            this.className = className;
        }

        //进入方法
        @Override
        protected void onMethodEnter() {
            soltIndex = newLocal(Type.LONG_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LSTORE, soltIndex);
        }

        @Override
        protected void onMethodExit(int opcode) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LSTORE, soltIndex+1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "logger", "Lorg/slf4j/Logger");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBUilder", "<init>", "()V", false);
            mv.visitLdcInsn("时间为");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder", false);
            mv.visitVarInsn(LLOAD, soltIndex+1);
            mv.visitVarInsn(LLOAD, soltIndex);
            mv.visitInsn(LSUB);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBUilder", "append", "(J)Ljava/lang/StringBuilder", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "org/slf4j/Logger", "info", "(Ljava/lang/String;)V", false);
        }

    }
}

