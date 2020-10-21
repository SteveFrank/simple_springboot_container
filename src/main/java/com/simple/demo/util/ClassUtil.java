package com.simple.demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ClassUtil {
    public static Class<?>[] getAllClassesInPackage(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new LinkedList<>();
            List<URL> jars = new LinkedList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if ("file".equals(protocol)) {
                    // 本地自己可见的代码
                    dirs.add(new File(resource.getFile()));
                } else if ("jar".equals(protocol)) {
                    // 引用第三方jar的代码
                    jars.add(resource);
                }
            }
            List<Class<?>> classes = new LinkedList<>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
            for (URL url : jars) {
                classes.addAll(findClasses(url, packageName));
            }
            return classes.toArray(new Class[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

    private static List<Class<?>> findClasses(URL url, String packageName) {
        List<Class<?>> classes = new LinkedList<>();
        try {
            JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();
                String className = jarEntryName.replace("/", ".");
                if (className.startsWith(packageName) && className.endsWith(".class")) {
                    classes.add(loadClass(className.substring(0, className.length() - 6)));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("parse class failed from " + url.toString(), e);
        }
        return classes;
    }

    private static ClassLoader getTCL() {
        ClassLoader cl;
        if (System.getSecurityManager() == null) {
            cl = Thread.currentThread().getContextClassLoader();
        } else {
            cl = java.security.AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
        }

        return cl;
    }

    public static Class<?> loadClass(final String className) throws ClassNotFoundException {
        try {
            return getTCL().loadClass(className);
        } catch (final Throwable e) {
            return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        }
    }

}
