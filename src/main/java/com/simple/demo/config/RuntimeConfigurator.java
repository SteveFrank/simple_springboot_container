package com.simple.demo.config;


public class RuntimeConfigurator {

	private static final RuntimeConfigurator INSTANCE = new RuntimeConfigurator();

	private RuntimeConfigurator() {

	}

	public static RuntimeConfigurator getConfig() {
		return INSTANCE;
	}

	private int classesCount;
	private int interfaceCount;
	private int abstractClassCount;

	public int getClassesCount() {
		return classesCount;
	}

	public void setClassesCount(int classesCount) {
		this.classesCount = classesCount;
	}

	public int getInterfaceCount() {
		return interfaceCount;
	}

	public void setInterfaceCount(int interfaceCount) {
		this.interfaceCount = interfaceCount;
	}

	public int getAbstractClassCount() {
		return abstractClassCount;
	}

	public void setAbstractClassCount(int abstractClassCount) {
		this.abstractClassCount = abstractClassCount;
	}

}
