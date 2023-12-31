package com.squaretest;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Objects;

/**
 * 逆向Demo
 *
 * @author brady
 * @date 2023/07/20
 */
@Slf4j
public class ReverseDemo {

    public static void main(String[] args) throws Exception {
        String classPath = Objects.requireNonNull(ReverseDemo.class.getResource("/")).getPath();
        File file = new File(classPath);
        String[] files = file.list((dir, name) -> name.endsWith(".jar") && name.startsWith("TestStarter"));
        assert files != null;
        String testStarter = files[0];
        ClassPool.getDefault().insertClassPath(classPath + testStarter);
        CtClass fClass = ClassPool.getDefault().getCtClass("com.squaretest.c.f");
        CtMethod a = fClass.getDeclaredMethod("a", new CtClass[0]);
        a.setBody("{return 8848;}");
        fClass.writeFile(classPath);

        CtClass iClass = ClassPool.getDefault().getCtClass("com.squaretest.c.i");
        iClass.getDeclaredMethod("b", new CtClass[]{ClassPool.getDefault().get("java.lang.String")})
                .setBody("{return true;}");
        iClass.writeFile(classPath);

        CtClass qClass = ClassPool.getDefault().getCtClass("com.squaretest.c.q");
        for (CtMethod declaredMethod : qClass.getDeclaredMethods("a")) {
            declaredMethod.setBody("{return true;}");
        }
        qClass.writeFile(classPath);
        String osName = System.getProperty("os.name");

        // 设置试用时间为固定值
        StringBuilder sb = new StringBuilder();
        String scriptName;
        if (osName.startsWith("Window")) {
            classPath = classPath.substring(1);
            sb.insert(0, "@echo off\n");
            scriptName = "replace.bat";
        } else {
            sb.insert(0, "#!/bin/bash\n");
            scriptName = "replace.sh";
        }
        // 无限试用
        sb.append("jar -uvf ").append(classPath).append(testStarter).append(" com/squaretest/c/f.class\n");
        sb.append("jar -uvf ").append(classPath).append(testStarter).append(" com/squaretest/c/i.class\n");
        // sb.append("jar -uvf ").append(classPath).append(testStarter).append(" com/squaretest/c/q.class\n");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(classPath + scriptName))) {
            bufferedWriter.write(sb.toString());
        }
    }
}


