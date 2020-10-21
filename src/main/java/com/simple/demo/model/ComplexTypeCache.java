package com.simple.demo.model;

import java.util.HashMap;

public class ComplexTypeCache extends HashMap<String, ComplexType> {

	private static final ComplexTypeCache INSTANCE = new ComplexTypeCache();

	private ComplexTypeCache() {

	}

	public static ComplexTypeCache getComplexTypeCache() {
		return INSTANCE;
	}
}
