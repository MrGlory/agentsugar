package org.ycz;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class LogTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            //通过包名获取类文件
            System.out.println(className);
            CtClass cc = ClassPool.getDefault().get(className);
            System.out.println();
            System.out.println(className);
            //获得指定方法名的方法
            System.out.println("annotations:[");
            System.out.println();
            for (Object o : cc.getAnnotations()) {
                System.out.println(o.toString());
            }
            System.out.println("]");
            System.out.println();
            System.out.println("method:[");
            for (CtMethod ctMethod : cc.getMethods()) {
                System.out.println(ctMethod.getName());
            }
            System.out.println("]");
            System.out.println();
//            CtMethod m = cc.getDeclaredMethod("doSomeThing");
//            //在方法执行前插入代码
//            m.insertBefore("{ System.out.println(\"记录日志\"); }");
            return null;
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
