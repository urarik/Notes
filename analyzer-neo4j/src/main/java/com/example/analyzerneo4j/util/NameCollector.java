package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.entity.*;
import com.example.analyzerneo4j.entity.Class;
import com.example.analyzerneo4j.entity.Package;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class NameCollector extends VoidVisitorAdapter<Void> {
    private final Mapper mapper;
    private Package packageEntity;
    private final String urlString;
    private final Project project;

    public NameCollector(Mapper mapper, String urlString, Project project) {
        this.mapper = mapper;
        this.urlString = urlString;
        this.project = project;
    }

    @Override
    public void visit(CompilationUnit n, Void arg) {
        String path;
        PackageDeclaration declaration = n.getPackageDeclaration().orElse(null);
        if(declaration == null) path = "";
        else path = declaration.getNameAsString();

        if(mapper.packages.get(path) == null) {
            putPackage(path);
        } else packageEntity = mapper.packages.get(path);

        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        super.visit(n, arg);
        //TODO inner interface

        if(n.isInterface()) {
            putInterface(n);
        } else if(n.isInnerClass()) {
            putClass(n);
        } else if(n.isLocalClassDeclaration()){
            // TODO
        } else {
            putClass(n);
        }
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        super.visit(n, arg);

        putMember(n);
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        super.visit(n, arg);

        putMethod(n);
    }

    private void putPackage(String path) {
        if(path == null) path = "";
        packageEntity = new Package(path);
        project.getPackages().add(packageEntity);

        mapper.packages.put(path, packageEntity);
    }

    private Class putClass(ClassOrInterfaceDeclaration declaration) {
        String name = declaration.getNameAsString();
        Class classEntity = new Class(
                urlString,
                name,
                declaration.isAbstract(),
                declaration.isStatic(),
                packageEntity
        );
        mapper.classes.put(ParsingUtils.getKey(packageEntity.getPath(), name), classEntity);
        return classEntity;
    }

    private Interface putInterface(ClassOrInterfaceDeclaration declaration) {
        String name = declaration.getNameAsString();
        Interface interfaceEntity = new Interface(
                urlString,
                name,
                packageEntity
        );
        mapper.interfaces.put(ParsingUtils.getKey(packageEntity.getPath(), name), interfaceEntity);
        return interfaceEntity;
    }

    private void putMember(FieldDeclaration fd) {
        String modifier;
        NodeList<Modifier> modifierList = fd.getModifiers();
        if(modifierList.isEmpty()) modifier = "package-private";
        else modifier = modifierList.get(0).toString().trim();

        if(!(fd.getParentNode().get() instanceof ClassOrInterfaceDeclaration)) return;
        ClassOrInterfaceDeclaration parentDeclaration = (ClassOrInterfaceDeclaration) fd.getParentNode().get();
        String parent = ParsingUtils.getKey(packageEntity.getPath(), parentDeclaration.getNameAsString());

        Boolean isStatic = fd.isStatic();
        Map<String, Member> set = mapper.members.computeIfAbsent(parent, k -> new HashMap<>());
        fd.getVariables().stream()
                .map(v -> new Member(modifier, v.getNameAsString(), isStatic))
                .forEach(member -> set.put(member.getName(), member));
    }

    private void putMethod(MethodDeclaration md) {
        String modifier;
        NodeList<Modifier> modifierList = md.getModifiers();
        if(modifierList.isEmpty()) modifier = "package-private";
        else modifier = modifierList.get(0).toString().trim();

        // 익명 클래스인 경우는 제외 (e.g. getNid() in NoteServiceTest.java )
        if(!(md.getParentNode().get() instanceof ClassOrInterfaceDeclaration)) return;
        ClassOrInterfaceDeclaration parentDeclaration = (ClassOrInterfaceDeclaration) md.getParentNode().get();
        String parent = ParsingUtils.getKey(packageEntity.getPath(), parentDeclaration.getNameAsString());
        String name = md.getNameAsString();

        Method method = new Method(modifier, name, md.isStatic());

        Map<String, Method> map = mapper.methods.computeIfAbsent(parent, k -> new TreeMap<>());
        map.put(ParsingUtils.getMethodKey(packageEntity.getPath(), md), method);

        putParameters(md, method);
    }

    private void putParameters(MethodDeclaration md, Method method) {
        Map<String, Parameter> map = mapper.parameters.computeIfAbsent(method, k -> new HashMap<>());

        md.getParameters().stream()
                .map(parameter -> new Parameter(parameter.getNameAsString(), method))
                .forEach(parameter -> map.put(parameter.getName(), parameter));
    }
}
