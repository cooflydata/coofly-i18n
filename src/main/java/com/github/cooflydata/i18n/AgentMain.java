package com.github.cooflydata.i18n;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class AgentMain {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Eclipse Agent started.");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException {
                try {
                    if (className.equals("org/eclipse/swt/widgets/Label") || className.equals("org/eclipse/swt/widgets/MenuItem")) {
                        CtClass clazz = null;
                        clazz = new ClassPool(true).get(className.replace('/', '.'));

                        StringBuilder loggerDeclaration = new StringBuilder();
                        loggerDeclaration.append("private static final java.util.logging.Logger logger = ");
                        loggerDeclaration.append("java.util.logging.Logger.getLogger(\"").append(clazz.getName()).append("\");\n");
                        clazz.addField(CtField.make(loggerDeclaration.toString(), clazz));


                        CtMethod setTextMethod = clazz.getDeclaredMethod("setText");
                        StringBuilder src = new StringBuilder();
                        src.append("{\n");
                        src.append("    logger.info(\"Setting text to: \" + $1);\n");
                        src.append("}");
                        setTextMethod.insertBefore(src.toString());
                        return clazz.toBytecode();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return classfileBuffer;
            }
        });
    }
}