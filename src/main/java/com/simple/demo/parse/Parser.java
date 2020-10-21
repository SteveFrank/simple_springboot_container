package com.simple.demo.parse;

import com.alibaba.fastjson.JSON;
import com.simple.demo.model.ComplexType;
import com.simple.demo.model.ComplexTypeCache;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class Parser {

	public void parse(String jarFileName) {
		JarFile jarFile = null;
		try {
			System.out.println("Parsing the jar file : " + jarFileName);
			// 加载*.jar文件
			jarFile = new JarFile(jarFileName);
			Enumeration<JarEntry> jarFileEntry = jarFile.entries();
			// 遍历jar包文件中的*.class类
			while (jarFileEntry.hasMoreElements()) {
				JarEntry entry = jarFileEntry.nextElement();
				// 抛弃其他的非class文件 如配置文件、xml文件等信息
				if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
					continue;
				}
				// 加载com.weibo.oasis.service 下的class文件
				if (entry.getName().contains("oasis")) {
					// 获取完整的 class信息
					String fullyQualifiedName = entry.getName().substring(0, entry.getName().length() - 6);
					fullyQualifiedName = fullyQualifiedName.replace('/', '.');
					String className = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
					// 提取类信息存储到cache
					ComplexType complexType = new ComplexType()
							.setName(className)
							.setFullyQualifiedName(fullyQualifiedName);
					// key -> fully qualified name
					// value -> complex type;
					ComplexTypeCache.getComplexTypeCache().put(fullyQualifiedName, complexType);
					System.out.println(JSON.toJSON(complexType));
				}
			}
			System.out.println("Parsing complete for jar : " + jarFileName);
		} catch (IOException exception) {
			System.err.println("Exception occurred while parsing the jar file");
		} finally {
			try {
				if (null != jarFile) {
					jarFile.close();
				}
			} catch (IOException exception) {
				System.err.println("Exception occurred while closing the jar file");
			}
		}
	}
}
