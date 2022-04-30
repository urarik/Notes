package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.entity.Class;
import com.example.analyzerneo4j.entity.Interface;
import com.example.analyzerneo4j.entity.Member;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TypeSolver {
    private final Mapper mapper;
    private final String packageString;
    private final Map<String, String> importMap; // import com.~~~.Class
    private final Set<String> stars; // import com.~~~.*;

    public TypeSolver(Mapper mapper, String packageDeclaration, NodeList<ImportDeclaration> importDeclarationList) {
        this.mapper = mapper;
        this.packageString = packageDeclaration;

        importMap = new HashMap<>();
        stars = new HashSet<>();

        importDeclarationList.stream()
                .filter(Predicate.not(ImportDeclaration::isAsterisk))
                .map(ImportDeclaration::getName)
                .map(Name::asString)
                .forEach(path -> {
                    if(path.lastIndexOf('.') != -1) {
                        String type = path.substring(path.lastIndexOf('.') + 1);
                        importMap.put(type, path);
                    } else importMap.put(path, path);
                });
        importDeclarationList.stream()
                .filter(ImportDeclaration::isAsterisk)
                .map(ImportDeclaration::getName)
                .map(Name::asString)
                .forEach(stars::add);

    }

    public Optional<Class> findClass(String type) {
        String path = importMap.get(type);
        if(path != null) { // import 문에 명시적으로 나와있는 경우
            Class customClass = mapper.classes.get(path);
            if(customClass == null) return Optional.empty();
            else return Optional.of(mapper.classes.get(path));
        } else {
            // 같은 패키지
            Class samePackage = mapper.classes.get(packageString+"."+type);
            if(samePackage != null) return Optional.of(samePackage);

            // import * 검사
            for(String star: stars) {
                String newPath = star + "." + type;
                Class diffPackage = mapper.classes.get(newPath);
                if(diffPackage != null) return Optional.of(diffPackage);
            }
        }
        
        // 사용자 작성 클래스가 아닌 경우
        return Optional.empty();
    }

    public Optional<Interface> findInterface(String type) {
        String path = importMap.get(type);
        if(path != null) { // import 문에 명시적으로 나와있는 경우
            Interface customInterface = mapper.interfaces.get(path);
            if(customInterface == null) return Optional.empty();
            else return Optional.of(mapper.interfaces.get(path));
        } else {
            // 같은 패키지
            Interface samePackage = mapper.interfaces.get(packageString+"."+type);
            if(samePackage != null) return Optional.of(samePackage);

            // import * 검사
            for(String star: stars) {
                String newPath = star + "." + type;
                Interface diffPackage = mapper.interfaces.get(newPath);
                if(diffPackage != null) return Optional.of(diffPackage);
            }
        }

        // 사용자 작성 클래스가 아닌 경우
        return Optional.empty();
    }

    public ContainerOptional findType(String type) {
        Class aClass = findClass(type).orElse(null);
        Interface anInterface = findInterface(type).orElse(null);

        return new ContainerOptional(aClass, anInterface);
    }

    public ContainerOptional findTypeWithKey(String key) {
        return new ContainerOptional(
                mapper.classes.get(key),
                mapper.interfaces.get(key)
        );
    }

    // name: variable name
    public String getTypeName(String path, String name, Expression top) {
        // 1. Parameter
        MethodDeclaration declaration = top.findAncestor(MethodDeclaration.class).get();
        var matchedParameter = declaration.getParameters().stream()
                .filter(parameter -> parameter.getNameAsString().equals(name))
                .findFirst();
        if(matchedParameter.isPresent()) return matchedParameter.get().getTypeAsString();

        // 2. variable
        StringBuilder typeName = new StringBuilder();
        Optional<String> variableOptional = top.findAncestor(MethodDeclaration.class).get()
                        .findAll(VariableDeclarationExpr.class).stream()
                        .filter(expr -> expr.getVariables().stream().anyMatch(variableDeclarator ->
                                variableDeclarator.getNameAsString().equals(name)))
                        .map(VariableDeclarationExpr::toString)
                        .findAny();

        if(variableOptional.isPresent()) {
            String expr = variableOptional.get();
            String type = expr.substring(0, expr.indexOf(' '));
            if(type.indexOf('<') != -1)
                return expr.substring(0, expr.indexOf(' ')).substring(0, expr.indexOf('<'));
            else return type;
        }

        // 3. field
        String className;
        Node node = top;
        while(true) {
            Optional<ClassOrInterfaceDeclaration> parent = node.findAncestor(ClassOrInterfaceDeclaration.class);
            if(parent.isEmpty()) break;
            node = parent.get();
            className = parent.get().getNameAsString();

            var members = mapper.members.get(ParsingUtils.getKey(path, className));
            if(members == null) continue;

            Member member = mapper.members.get(ParsingUtils.getKey(path, className)).get(name);
            if (member != null) {
                if (member.getClassType() != null)
                    return member.getClassType().getName();
                else return member.getInterfaceType().getName();
            }
        }

        // TODO 4. lambda parameter
        // 타입 추론은 시간이 오래걸릴 것 같아서 일단 패스
        return "";
    }

    public Optional<Class> findSuperClass(Class aClass) {
        return mapper.classes.values().stream()
                .filter(cand -> cand.getClassRelates().stream()
                    .anyMatch(relate -> relate.getFrom().equals(aClass) &&
                            relate.getType().equals("Generalization")))
                .findAny();

    }
}
