package com.simple.demo.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderUtil {
    private static Method CLASS_LOADER_ADD_URL_METHOD;

    static {
        try {
            CLASS_LOADER_ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            CLASS_LOADER_ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void addUrlToClassLoader(URLClassLoader loader, URL url) {
        try {
            CLASS_LOADER_ADD_URL_METHOD.invoke(loader, url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * convert the loader to URLClassLoader and call addUrlToClassLoader(URLClassLoader loader, URL url)
     * @param loader
     * @param url
     */
    public static void addUrlToClassLoader(ClassLoader loader, URL url) {
        addUrlToClassLoader((URLClassLoader) loader, url);
    }
}
