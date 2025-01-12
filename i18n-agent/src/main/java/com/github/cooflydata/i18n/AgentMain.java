package com.github.cooflydata.i18n;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;

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

/**
 * @author 东来
 * @email 80871901@qq.com
 */
public class AgentMain {

    private static final String LOG_FILE_NAME = "D:/i18n.log";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE_NAME, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            String logMessage = String.format("%s %s%n", dateFormat.format(new Date()), message);
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
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                try {
                    log(className);
                    if ("org/eclipse/swt/widgets/Label".equals(className)
                            || "org/eclipse/swt/widgets/MenuItem".equals(className)
                            || "org/eclipse/swt/widgets/Button".equals(className)
                            || "org/eclipse/swt/custom/CTabItem".equals(className)
                    ) {
                        ClassPool cp = ClassPool.getDefault();
                        cp.insertClassPath(new LoaderClassPath(loader));

                        CtClass clazz = cp.get(className.replace('/', '.'));
                        clazz.addField(CtField.make("static java.io.PrintWriter logWriter;", clazz));
                        clazz.addField(CtField.make("static com.github.cooflydata.i18n.Translator translator = new com.github.cooflydata.i18n.Translator();", clazz));
                        clazz.addField(CtField.make("static final java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");", clazz));

                        CtMethod logMethod = CtMethod.make("""
                                void log(String msg) {
                                    try {
                                        if (logWriter == null) {
                                            logWriter = new java.io.PrintWriter(new java.io.FileOutputStream("D:/i18n_agent.log", true)); // 追加模式
                                        }
                                    } catch (java.io.IOException e) {
                                        System.err.println("Failed to create log file: " + e.getMessage());
                                    }
                                    if (logWriter != null) {
                                        String timestamp = dateFormat.format(new java.util.Date());
                                        logWriter.println(timestamp + " : "  + msg);
                                        logWriter.flush();
                                    }
                                }""", clazz);

                        clazz.addMethod(logMethod);

                        CtMethod setTextMethod = clazz.getDeclaredMethod("setText");
                        setTextMethod.insertBefore("""
                                {
                                    String _raw_text = $1;
                                    $1 = translator.translate(getClass().getName(), $1);
                                    log(getClass().getName() + " => "+ _raw_text + " : " + $1);
                                }
                                """);
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