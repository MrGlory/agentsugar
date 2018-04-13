package org.ycz;
import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Agent {
    public static void agentmain(String args, Instrumentation inst) {
        //将依赖jar包通过目标程序AppClassLoader加载到运行时中
       System.out.println("dependencies load start...");
       try {
           String path = "file:///E://workspace/log/target/log-1.0-SNAPSHOT-jar-with-dependencies.jar";
           URL jarPath = new URL(path);
           ClassLoader classloader = Thread.currentThread().getContextClassLoader();

           URLClassLoader urlClassloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
           Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
           method.setAccessible(true);
           method.invoke(urlClassloader, jarPath);
           String name = "org.ycz.util.LogFactory";
           classloader.loadClass(name);
       } catch (InvocationTargetException e) {
           e.printStackTrace();
       } catch (NoSuchMethodException e) {
           e.printStackTrace();
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       }
        System.out.println("dependencies load done...");
        //加载完成

        //获取目前运行时的所有Class文件
        Class[] classes = inst.getAllLoadedClasses();
        System.out.println("scan target class start...");
        for(Class cls :classes){
            String className = cls.getName();
            try {
                CtClass cc = ClassPool.getDefault().get(className);
                for (Object o : cc.getAnnotations()) {
                    if ("@org.springframework.web.bind.annotation.RestController".equals(o.toString())){
                        System.out.println("--------------"+className+"--------------");
                        for (CtMethod ctMethod : cc.getDeclaredMethods()) {
                            System.out.println(className+"."+ctMethod.getName()+" is modifying...");
                            ctMethod.insertBefore("org.ycz.util.LogFactory.writeEnterLog(\""+className+"\",\""+ctMethod.getName()+"\",$1); ");
                            ctMethod.insertBefore("System.out.println($$);");
                            ctMethod.insertBefore("System.out.println($1);");
                            System.out.println(className+"."+ctMethod.getName()+" is modified...");

                        }
                        System.out.println("write new class into file start...");
                        inst.redefineClasses(new ClassDefinition(cls,cc.toBytecode()));
                        System.out.println("write new class into file started...");
                    }
                }
            }catch (NotFoundException e){
//                System.out.println("javaassist not found class:"+className);
            } catch (IOException e) {
                System.out.println("IOException "+e.getMessage());
            } catch (CannotCompileException e) {
                System.out.println("compile Error "+e.getMessage());
            } catch (UnmodifiableClassException e) {
                System.out.println("modify class exception "+e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFound "+e.getMessage());
            }
        }
        System.out.println("scan target class done...");
        System.out.println("agent started...");
    }
}