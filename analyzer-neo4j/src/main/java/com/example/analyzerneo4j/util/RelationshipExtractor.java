package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.entity.*;
import com.example.analyzerneo4j.entity.Class;
import com.example.analyzerneo4j.repository.ClassRepository;
import com.example.analyzerneo4j.repository.InterfaceRepository;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

public class RelationshipExtractor extends VoidVisitorAdapter<Void> {
    private final Mapper mapper;
    private final ClassRepository classRepository;
    private final InterfaceRepository interfaceRepository;
    private TypeSolver typeSolver;
    private String path;
    private Class selfClass;
    private Interface selfInterface;

    public RelationshipExtractor(Mapper mapper, EntityContainer entityContainer) {
        this.mapper = mapper;
        this.classRepository = entityContainer.classRepository;
        this.interfaceRepository = entityContainer.interfaceRepository;
    }

    public void analyze(CompilationUnit cu) {
        if(cu.getPackageDeclaration().isPresent())
            path = cu.getPackageDeclaration().get().getNameAsString();
        else path = "";
        typeSolver = new TypeSolver(mapper, path, cu.getImports());

        visit(cu, null);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration cid, Void arg) {
        super.visit(cid, arg);

        selfClass = mapper.classes.get(ParsingUtils.getKey(path, cid.getNameAsString()));
        if(selfClass == null)
            selfInterface = mapper.interfaces.get(ParsingUtils.getKey(path, cid.getNameAsString()));

        cid.getExtendedTypes().stream()
                .map(ClassOrInterfaceType::getNameAsString)
                .map(typeSolver::findClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(to -> {
                    if(selfClass != null)
                        to.getClassRelates().add(new ClassRelationship("Generalization", selfClass));
                    else to.getInterfaceRelates().add(new InterfaceRelationship("Generalization", selfInterface));
                });

        cid.getImplementedTypes().stream()
                .map(ClassOrInterfaceType::getNameAsString)
                .map(typeSolver::findInterface)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(to -> {
                    if(selfClass != null)
                        to.getClassRelates().add(new ClassRelationship("Realization", selfClass));
                    else to.getInterfaceRelates().add(new InterfaceRelationship("Realization", selfInterface));
                });

        if(cid.isInnerClass()) {
            Optional<Node> parent = Optional.of(cid);
            while(true) {
                if(parent.isEmpty()) break;
                parent = parent.get().getParentNode();
                if(parent.isPresent() && parent.get() instanceof ClassOrInterfaceDeclaration) {
                    String name = cid.getNameAsString();
                    String parentName = ((ClassOrInterfaceDeclaration) parent.get()).getNameAsString();
                    if(((ClassOrInterfaceDeclaration) parent.get()).isInterface()) {
                        mapper.interfaces.get(ParsingUtils.getKey(path, parentName)).getNested().add(selfClass);
                    } else {
                        mapper.classes.get(ParsingUtils.getKey(path, parentName)).getNested().add(selfClass);
                    }
                    break;
                }
            }
        }

    }

    @Override
    public void visit(FieldDeclaration fd, Void arg) {
        super.visit(fd, arg);

        String typeString = fd.getCommonType().toString();

        if(!(fd.getParentNode().get() instanceof ClassOrInterfaceDeclaration)) return;
        ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) fd.getParentNode().get();
        selfClass = mapper.classes.get(ParsingUtils.getKey(path, cid.getNameAsString()));
        if(selfClass == null)
            selfInterface = mapper.interfaces.get(ParsingUtils.getKey(path, cid.getNameAsString()));


        String parent = ParsingUtils.getKey(path, cid.getNameAsString());
        String relationship = (fd.isFinal())? "Composition": "Association";
        for(var variable: fd.getVariables()) {
            Member member = mapper.members.get(parent).get(variable.getNameAsString());
            member.setAClass(selfClass);

            ContainerOptional type;
            if(typeString.contains("<")) {
                String innerTypeString = typeString.substring(typeString.indexOf("<") + 1, typeString.indexOf(">"));
                type = typeSolver.findType(innerTypeString);

                // 사용자 정의 클래스가 아니라면 singleton class로 표현. 없으면 생성
                if(type.empty()) {
                    member.setClassType(getClass(typeString));
                } else if(type.isClass()) {
                    Class aClass = type.getAClass();
                    member.setClassType(getClass(typeString)); // e.g. List<aClass>
                    if(selfClass != null)
                        aClass.getClassRelates().add(new ClassRelationship(relationship, selfClass));
                    else aClass.getInterfaceRelates().add(new InterfaceRelationship(relationship, selfInterface));
                } else { // interface
                    Interface anInterface = type.getAnInterface();
                    member.setInterfaceType(getInterface(typeString));
                    if(selfClass != null)
                        anInterface.getClassRelates().add(new ClassRelationship(relationship, selfClass));
                    else anInterface.getInterfaceRelates().add(new InterfaceRelationship(relationship, selfInterface));
                }
            } else {
                type = typeSolver.findType(typeString);

                // 사용자 정의 클래스가 아니라면 singleton class로 표현. 없으면 생성
                if(type.empty()) {
                    member.setClassType(getClass(typeString));
                } else if(type.isClass()) {
                    Class aClass = type.getAClass();
                    member.setClassType(aClass);
                    if(selfClass != null)
                        aClass.getClassRelates().add(new ClassRelationship(relationship, selfClass));
                    else aClass.getInterfaceRelates().add(new InterfaceRelationship(relationship, selfInterface));
                } else { // interface
                    Interface anInterface = type.getAnInterface();
                    member.setInterfaceType(anInterface);
                    if(selfClass != null)
                        anInterface.getClassRelates().add(new ClassRelationship(relationship, selfClass));
                    else anInterface.getInterfaceRelates().add(new InterfaceRelationship(relationship, selfInterface));
                }
            }
        }
    }

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);

        if(!(md.getParentNode().get() instanceof ClassOrInterfaceDeclaration)) return;
        ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) md.getParentNode().get();
        selfClass = mapper.classes.get(ParsingUtils.getKey(path, cid.getNameAsString()));
        if(selfClass == null)
            selfInterface = mapper.interfaces.get(ParsingUtils.getKey(path, cid.getNameAsString()));

        Method selfMethod = mapper.methods.get(
                ParsingUtils.getKey(path, cid.getNameAsString()))
                .get(ParsingUtils.getMethodKey(path, md));
        if(selfClass != null) selfMethod.setAClass(selfClass);
        else selfMethod.setAnInterface(selfInterface);

        String returnType = md.getTypeAsString();
        ContainerOptional type = typeSolver.findType(returnType);
        if(!type.empty()) {
            if(type.isClass()) {
                Class aClass = type.getAClass();
                if(selfClass != null)
                    aClass.getClassRelates().add(new ClassRelationship("Dependency", selfClass));
                else aClass.getInterfaceRelates().add(new InterfaceRelationship("Dependency", selfInterface));
                selfMethod.setClassReturnType(aClass);
            } else {
                Interface anInterface = type.getAnInterface();
                if(selfClass != null)
                    anInterface.getClassRelates().add(new ClassRelationship("Dependency", selfClass));
                else anInterface.getInterfaceRelates().add(new InterfaceRelationship("Dependency", selfInterface));
                selfMethod.setInterfaceReturnType(anInterface);
            }
        } else selfMethod.setClassReturnType(getClass(returnType));

        for(Parameter parameter: md.getParameters()) {
            com.example.analyzerneo4j.entity.Parameter parameterEntity =
                mapper.parameters.get(selfMethod).get(parameter.getNameAsString());

            ContainerOptional containerOptional = typeSolver.findType(parameter.getType().asString());
            if(containerOptional.present()) {
                if(containerOptional.isClass()) {
                    if(selfClass != null)
                        containerOptional.getAClass().getClassRelates().add(new ClassRelationship("Dependency", selfClass));
                    else
                        containerOptional.getAClass().getInterfaceRelates().add(new InterfaceRelationship("Dependency", selfInterface));
                    parameterEntity.setClassType(containerOptional.getAClass());
                }
                else {
                    if(selfClass != null)
                        containerOptional.getAnInterface().getClassRelates().add(new ClassRelationship("Dependency", selfClass));
                    else containerOptional.getAnInterface().getInterfaceRelates().add(new InterfaceRelationship("Dependency", selfInterface));
                    parameterEntity.setInterfaceType(containerOptional.getAnInterface());
                }
            } else parameterEntity.setClassType(getClass(parameter.getType().asString()));
        }

    }

    @Override
    public void visit(ObjectCreationExpr oce, Void arg) {
        super.visit(oce, arg);

        selfClass = null; selfInterface = null;
        oce.walk(Node.TreeTraversal.PARENTS, node -> {
            if(selfClass == null && selfInterface == null && node instanceof ClassOrInterfaceDeclaration) {
                selfClass = mapper.classes.get(ParsingUtils.getKey(path, ((ClassOrInterfaceDeclaration) node).getNameAsString()));
                selfInterface = mapper.interfaces.get(ParsingUtils.getKey(path, ((ClassOrInterfaceDeclaration) node).getNameAsString()));
            }
        });

        String name = oce.getType().getNameAsString();
        ContainerOptional type = typeSolver.findType(name);
        if(type.present()) {
            if(type.isClass()) {
                if(selfClass != null)
                    type.getAClass().getClassRelates().add(new ClassRelationship("Dependency", selfClass));
                else type.getAClass().getInterfaceRelates().add(new InterfaceRelationship("Dependency", selfInterface));
            }
            else {
                if(selfClass != null)
                    type.getAnInterface().getClassRelates().add(new ClassRelationship("Dependency", selfClass));
                else type.getAnInterface().getInterfaceRelates().add(new InterfaceRelationship("Dependency", selfInterface));
            }
        }
    }

    private Class getClass(String typeString) {
        // 다른 사용자정의 클래스를 가져올 수 있음.
        // custom property를 추가해서 해결해야할듯
        Class target = classRepository.findClassByName(typeString).block();
        if(target == null)
            return classRepository.save(new Class("custom", typeString, false, false, null)).block();
        else return target;
    }
    private Interface getInterface(String typeString) {
        Interface target = interfaceRepository.findInterfaceByName(typeString).block();
        if(target == null)
            return interfaceRepository.save(new Interface("", typeString, null)).block();
        else return target;
    }
}
