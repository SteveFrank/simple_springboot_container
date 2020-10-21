package com.simple.demo.analyzer;

import com.alibaba.fastjson.JSON;
import com.simple.demo.config.RuntimeConfigurator;
import com.simple.demo.model.ComplexType;
import com.simple.demo.model.ComplexTypeCache;
import com.simple.demo.util.ClassLoaderUtil;
import com.simple.demo.util.ClassUtil;
import javafx.application.Application;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class StructureAnalyzer {

	private static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";
	private static final String BOOT_INF_LIB = "BOOT-INF/lib/";

	public void analyse(String jarFileName) throws IOException {

		try {
			System.out.println("Analysing jar file " + jarFileName);
			JarFile.registerUrlProtocolHandler();
			File file = new File(jarFileName);
			JarFileArchive archive = new JarFileArchive(file);
			List<Archive> archives = new ArrayList<>(
					archive.getNestedArchives(entry -> {
						if (entry.isDirectory()) {
							return entry.getName().equals(BOOT_INF_CLASSES);
						}
						return entry.getName().startsWith(BOOT_INF_LIB);
					})
			);
			List<URL> urls = new ArrayList<>(archives.size());
			for (Archive arc : archives) {
				urls.add(arc.getUrl());
			}
			URL[] urlArray = urls.toArray(new URL[0]);
			ClassLoader parentClassLoader = getClass().getClassLoader();
			LaunchedURLClassLoader launchedURLClassLoader = new LaunchedURLClassLoader(urlArray, parentClassLoader);
			for (Map.Entry<String, ComplexType> stringComplexTypeEntry : ComplexTypeCache.getComplexTypeCache().entrySet()) {
				ComplexType complexType = stringComplexTypeEntry.getValue();
				if (complexType.getFullyQualifiedName().contains("com")) {
					try {
						Class<?> loadedClass = launchedURLClassLoader.loadClass(complexType.getFullyQualifiedName());
						System.out.println("loaded class : " + complexType.getFullyQualifiedName());
						// interface abstract class 计数
						populateCounts(loadedClass);
//						Field[] fields = loadedClass.getDeclaredFields();
//						System.out.println(fields);
//						Method[] methods = loadedClass.getDeclaredMethods();
//						System.out.println(methods);
//						if (null != fields && fields.length >= 0) {
//							System.out.println(
//									"Class " + complexType.getFullyQualifiedName() + " has " + JSON.toJSONString(fields) + " fields");
//							// populate the fields data
//							populateFieldsData(fields, complexType);
//						}
//						if (null != methods && methods.length >= 0) {
//							System.out.println(
//									"Class " + complexType.getFullyQualifiedName() + " has " + JSON.toJSONString(methods) + " methods");
//							// populate the methods data
//							populateMethodsData(methods, complexType);
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Analysis complete for " + jarFileName);
		} catch (MalformedURLException exception) {
			System.out.println("Exception occurred while loading the classes from the jar file");
		}

	}

	private void populateCounts(Class<?> loadedClass) {
		if (Modifier.isInterface(loadedClass.getModifiers())) {
			// 是interface
			RuntimeConfigurator.getConfig()
					.setInterfaceCount(RuntimeConfigurator.getConfig().getInterfaceCount() + 1);
		} else if (Modifier.isAbstract(loadedClass.getModifiers())) {
			// 是abstract
			RuntimeConfigurator.getConfig()
					.setAbstractClassCount(RuntimeConfigurator.getConfig().getAbstractClassCount() + 1);
		} else {
			// 是class
			RuntimeConfigurator.getConfig()
					.setClassesCount(RuntimeConfigurator.getConfig().getClassesCount() + 1);
		}
	}

	private void populateMethodsData(Method[] methods, ComplexType complexType) {
		List<Method> methodDetails = new ArrayList<>();
		Collections.addAll(methodDetails, methods);
		complexType.setMethodDetails(methodDetails);
	}


	private void populateFieldsData(Field[] fields, ComplexType complexType) {
		HashMap<String, ComplexType> attributes = new HashMap<>();
		// for every field
		for (Field field : fields) {
			if (!field.getType().isPrimitive()) {
				populateNonPrimitiveFieldsData(field, attributes);
			} else {
				populatePrimitiveFieldsData(field, attributes);
			}
			complexType.setAttributes(attributes);
		}
	}

	private void populatePrimitiveFieldsData(Field field, HashMap<String, ComplexType> attributes) {
		ComplexType complexType = new ComplexType();
		complexType.setName(field.getType().getName());
		complexType.setFullyQualifiedName(field.getType().getName());
		attributes.put(field.getName(), complexType);
	}


	private void populateNonPrimitiveFieldsData(Field field, HashMap<String, ComplexType> attributes) {
		ComplexType complexType = ComplexTypeCache.getComplexTypeCache().get(field.getType().getName());
		if (null == complexType) {
			complexType = generateComplexType(field.getType().getName());
			complexType.setName(field.getType().getSimpleName());
			complexType.setFullyQualifiedName(field.getType().getSimpleName());
		}
		// FieldName *..1 FieldType
		attributes.put(field.getName(), complexType);

	}

	private ComplexType generateComplexType(String name) {
		ComplexType complexType = new ComplexType();
		complexType.setAttributes(new HashMap<>());
		complexType.setMethodDetails(new ArrayList<>());

		complexType.setName(name);
		return complexType;
	}
}
