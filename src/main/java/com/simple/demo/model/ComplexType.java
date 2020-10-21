package com.simple.demo.model;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

public class ComplexType {
	private String name;
	private String fullyQualifiedName;
	private Map<String, ComplexType> attributes = null;
	private List<Method> methodDetails = null;
	private boolean isGeneric;

	public List<Method> getMethodDetails() {
		return methodDetails;
	}

	public void setMethodDetails(List<Method> methodDetails) {
		this.methodDetails = methodDetails;
	}

	public String getName() {
		return name;
	}

	public ComplexType setName(String name) {
		this.name = name;
		return this;
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public ComplexType setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
		return this;
	}

	public Map<String, ComplexType> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, ComplexType> attributes) {
		this.attributes = attributes;
	}

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean generic) {
		isGeneric = generic;
	}
}
