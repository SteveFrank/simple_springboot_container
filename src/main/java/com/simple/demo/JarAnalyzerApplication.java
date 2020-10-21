package com.simple.demo;

import com.simple.demo.analyzer.StructureAnalyzer;
import com.simple.demo.parse.Parser;

import java.io.IOException;

/**
 * @author yangqian
 * @date 2020/10/16
 */
public class JarAnalyzerApplication {

    private final Parser parser = new Parser();
    private final StructureAnalyzer structureAnalyser = new StructureAnalyzer();
    // 自己的jar包位置
    private static final String JAR_FILE_NAME = ".../simple-springboot-container/jar/xxx-1.0.jar";

    public static void main(String[] args) throws IOException {
        JarAnalyzerApplication jarAnalyzerApplication = new JarAnalyzerApplication();
        jarAnalyzerApplication.parser.parse(JAR_FILE_NAME);
        jarAnalyzerApplication.structureAnalyser.analyse(JAR_FILE_NAME);
    }

}
