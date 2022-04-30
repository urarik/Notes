package com.example.analyzerneo4j.util;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ParsingUtils {

    public static String getKey(String path, String name) {
        return path+"."+name;
    }
    public static String getKey(ContainerOptional optional) {
        if(optional.empty()) return null;

        String name, path;
        if(optional.isClass()) {
            name = optional.getAClass().getName();
            // 사용자 작성 클래스 X
            // e.g. List, String . . .
            if(optional.getAClass().getAPackage() == null)
                return null;
            path = optional.getAClass().getAPackage().getPath();
        }
        else {
            name = optional.getAnInterface().getName();
            // 사용자 작성 클래스 X
            // e.g. List, String . . .
            if(optional.getAnInterface().getAPackage() == null)
                return null;
            path = optional.getAnInterface().getAPackage().getPath();
        }
        return path+"."+name;
    }

    public static String getMethodKey(String path, MethodDeclaration md) {
        String name = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();
        StringBuilder stringBuilder = new StringBuilder(getKey(path, name));
        stringBuilder.append(".");
        stringBuilder.append(md.getNameAsString());
        md.getParameters().forEach(parameter -> stringBuilder.append("+").append(parameter.getTypeAsString()));

        return stringBuilder.toString();
    }
}
