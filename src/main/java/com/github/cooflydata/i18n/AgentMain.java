package com.github.cooflydata.i18n;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgentMain {

    private static final String LOG_FILE_NAME = "D:\\runtime-KNIME\\.metadata\\i18n.log";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE_NAME, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            String logMessage = String.format("%s %s%n", dateFormat.format(new Date()),message);
            printWriter.println(logMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Eclipse Agent started.");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException {
                try {
                    if ("org/eclipse/swt/widgets/Label".equals(className) || "org/eclipse/swt/widgets/MenuItem".equals(className)) {

                        ClassPool pool = ClassPool.getDefault();

                        log("1111 " + className);

                        CtClass clazz = null;
                        clazz = pool.get(className.replace('/', '.'));

                        log("22222" + clazz.getName());

                        clazz.addField(CtField.make("private static final String LOG_FILE_NAME = \"D:\\runtime-KNIME\\.metadata\\i18n.log\"", clazz));
                        clazz.addField(CtField.make("private static final SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\")", clazz));

                        clazz.addMethod(CtMethod.make("public static void log(String message) {\n" +
                                "        try (FileWriter fileWriter = new FileWriter(LOG_FILE_NAME, true);\n" +
                                "             PrintWriter printWriter = new PrintWriter(fileWriter)) {\n" +
                                "            String logMessage = String.format(\"%s %s%n\", dateFormat.format(new Date()),message);\n" +
                                "            printWriter.println(logMessage);\n" +
                                "        } catch (IOException e) {\n" +
                                "            throw new RuntimeException(e);\n" +
                                "        }\n" +
                                "    }", clazz));

//                        StringBuilder loggerDeclaration = new StringBuilder();
//                        loggerDeclaration.append("private static final java.util.logging.Logger logger = ");
//                        loggerDeclaration.append("java.util.logging.Logger.getLogger(\"").append(clazz.getName()).append("\");\n");
//                        clazz.addField(CtField.make(loggerDeclaration.toString(), clazz));

                        // Declare and initialize Logger instance
//                        StringBuilder loggerDeclaration = new StringBuilder();
//                        loggerDeclaration.append("private static final org.slf4j.Logger logger = ");
//                        loggerDeclaration.append("org.slf4j.LoggerFactory.getLogger(\"").append(clazz.getName()).append("\");\n");
//                        clazz.addField(CtField.make(loggerDeclaration.toString(), clazz));


                        CtMethod setTextMethod = clazz.getDeclaredMethod("setText");
                        StringBuilder src = new StringBuilder();
                        src.append("{\n");
                        src.append("    log(\"Setting text to: \" + $1);\n");
                        src.append("    $1  = \"--\" + $1 + \"--\"; \n");
                        src.append("}");
                        setTextMethod.insertBefore(src.toString());
                        return clazz.toBytecode();
                    }
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    System.out.println("Captured printStackTrace():\n" + sw);
                    log("Exception: " + sw);
                }
                return classfileBuffer;
            }
        });
    }
}