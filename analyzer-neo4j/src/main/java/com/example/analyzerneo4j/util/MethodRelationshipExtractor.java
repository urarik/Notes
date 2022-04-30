package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.entity.Class;
import com.example.analyzerneo4j.entity.Member;
import com.example.analyzerneo4j.entity.Method;
import com.example.analyzerneo4j.entity.MethodRelationship;
import com.example.analyzerneo4j.entity.State;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import javassist.expr.MethodCall;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import javax.swing.text.html.Option;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MethodRelationshipExtractor {
    private final Mapper mapper;
    /// method call "order"
    private final Container order = new Container(1L);
    // state "id"
    private final Container id = new Container(0L);
    private final Queue<State> states = new LinkedList<>();
    private final Map<MethodCallExpr, Boolean> callLogs = new HashMap<>();
    private TypeSolver typeSolver;
    private String path, name = null;
    private Class extend = null;
    private ContainerOptional self = null;

    public MethodRelationshipExtractor(Mapper mapper) {
        this.mapper = mapper;
    }

    public void analyze(CompilationUnit cu) {
        if(cu.getPackageDeclaration().isPresent())
            path = cu.getPackageDeclaration().get().getNameAsString();
        else path = "";

        typeSolver = new TypeSolver(mapper, path, cu.getImports());
        cu.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
            methodDeclaration.findAncestor(ClassOrInterfaceDeclaration.class).ifPresent(
                    declaration -> {
                        name = declaration.getNameAsString();
                        declaration.getExtendedTypes().stream()
                                .map(ClassOrInterfaceType::getNameAsString)
                                .map(typeSolver::findClass)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .findFirst()
                                .ifPresent(aClass -> extend = aClass);
                        self = typeSolver.findTypeWithKey(ParsingUtils.getKey(path, name));
                    }
            );
            if(name == null) name = "";

            if (name.equals("MessageServiceTest"))
                System.out.println("!");

            String key = ParsingUtils.getKey(path, name);
            Method method = mapper.methods.get(key).get(ParsingUtils.getMethodKey(path, methodDeclaration));
            traverse(methodDeclaration, method);
        });
    }

    private void traverse(Node root, Method method) {
        root.getChildNodes().forEach(node -> {
            NodeType nodeType = NodeType.INSTANCE.getType(node);
            if(nodeType == NodeType.LOOP) {
                processLoop((Statement) node, method);
            }
            else if (nodeType == NodeType.CONDITIONAL) {
                processConditional((IfStmt) node, method);
            }
            else if (nodeType == NodeType.METHOD_CALL) {
                processMethodCall((MethodCallExpr) node, method);
            }
            else traverse(node, method);
        });
    }

    private void processLoop(Statement node, Method method) {
        StringBuilder conditionBuilder = new StringBuilder();
        StringBuilder typeBuilder = new StringBuilder();
        if(node instanceof WhileStmt) {
            typeBuilder.append("While");
            conditionBuilder.append(((WhileStmt)node).getCondition());
        }
        else if (node instanceof ForEachStmt) {
            typeBuilder.append("ForEach");
            conditionBuilder.append(((ForEachStmt)node).getVariable());
            conditionBuilder.append(" : ");
            conditionBuilder.append(((ForEachStmt)node).getIterable());
        }
        else if (node instanceof ForStmt) {
            typeBuilder.append("For");
            conditionBuilder.append(((ForStmt)node).getInitialization().stream()
                    .map(Expression::toString)
                    .collect(Collectors.joining(", ")));
            conditionBuilder.append("; ");

            conditionBuilder.append(((ForStmt)node).getCompare().map(Node::toString).orElse(" "));

            conditionBuilder.append("; ");
            conditionBuilder.append(((ForStmt)node).getUpdate().stream()
                    .map(Expression::toString)
                    .collect(Collectors.joining(", ")));
        } else throw new InvalidParameterException();
        String condition = conditionBuilder.toString();
        String type = typeBuilder.toString();

        states.add(new State("PUSH", type, condition, id.postInc()));
        traverse(node.getChildNodes().get(node.getChildNodes().size() - 1), method);
        states.add(new State("POP"));
    }

    private void processMethodCall(MethodCallExpr expr, Method method) {
        Stack<Expression> stack = new Stack<>();

        try {
            if (callLogs.get(expr) == null) {
                Node node = expr;

                while (true) {
                    List<Node> children = node.getChildNodes();
                    stack.push((Expression) node);

                    if (children.size() == 0)
                        break;
                    // Method chain ( n.some1().some2() )
                    if (children.get(0) instanceof MethodCallExpr
                            || children.get(0) instanceof FieldAccessExpr
                            || children.get(0) instanceof SuperExpr
                            || children.get(0) instanceof ThisExpr) {
                        node = children.get(0);
                    }
                    // member method whose children[0] is variable
                    // ( n.something() )
                    else if (children.get(0) instanceof NameExpr) {
                        stack.push((Expression) children.get(0));
                        break;
                    }

                    // member method ( something() )
                    else if (children.get(0) instanceof SimpleName) {
                        break;
                    }
                    // FieldAccessExpr의 child인 경우로 식별됨
                    else if (children.get(0) instanceof Name) {
                        stack.push(new NameExpr(children.get(0).toString()));
                        break;
                    }
                }

                AtomicBoolean isEnd = new AtomicBoolean(false);
                String key;
                ContainerOptional optional = self;
                Expression top;

                while (!stack.isEmpty()) {
                    top = stack.pop();
                    if (top instanceof MethodCallExpr) {
                        processArguments(((MethodCallExpr) top).getArguments(), method);

                        if (!isEnd.get() && optional.present()) {
                            key = ParsingUtils.getKey(optional);
                            if(key == null) {
                                isEnd.set(true);
                                continue;
                            }
                            processRelationship(key, isEnd, (MethodCallExpr) top, method, optional);
                        }
                    } else if (top instanceof SuperExpr) {
                        if (!isEnd.get()) {
                            if (optional.isClass()) {
                                Optional<Class> superClassOptional = typeSolver.findSuperClass(optional.getAClass());
                                optional = new ContainerOptional(superClassOptional.orElse(null), null);
                            }
                        }
                    } else if (top instanceof ThisExpr) {
                        optional = optional;
                    } else if (top instanceof NameExpr) {
                        if (!isEnd.get()) {
                            String name = ((NameExpr) top).getNameAsString();
                            if (Character.isUpperCase(name.charAt(0))) { // Class
                                optional = typeSolver.findType(name);
                            } else { // Variable
                                String typeName = typeSolver.getTypeName(path, name, top);
                                if(typeName.equals("")) {
                                    isEnd.set(true);
                                    continue;
                                }

                                key = ParsingUtils.getKey(typeSolver.findType(typeName));
                                if(key == null) {
                                    isEnd.set(true);
                                    continue;
                                }
                                optional = typeSolver.findTypeWithKey(key);
                            }
                        }
                    } else if (top instanceof FieldAccessExpr) {
                        if (!isEnd.get() && optional.present()) {
                            key = ParsingUtils.getKey(optional);
                            if(key == null) {
                                isEnd.set(true);
                                continue;
                            }

                            Member member = mapper.members.get(
                                    key).get(((FieldAccessExpr) top).getName().toString());
                            optional = new ContainerOptional(member.getClassType(), member.getInterfaceType());
                        }
                    } else throw new RuntimeException();
                    if (!isEnd.get() && optional.empty()) isEnd.set(true);
                }
                callLogs.put(expr, true);
            }
        } catch (StackOverflowError e) {
            System.out.println(stack.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private void processRelationship(String key, AtomicBoolean isEnd, MethodCallExpr top, Method method, ContainerOptional optional) {
        if(key == null) isEnd.set(true);
        else {
            String methodName = top.getNameAsString();
            Method target = getMethod(key, methodName);

            if(target == null) {
                target = new Method("", methodName, false);
                target.setAClass(optional.getAClass());
                target.setAnInterface(optional.getAnInterface());
                String signature = key + "." + methodName;

                mapper.methods.get(key).put(signature, target);
                isEnd.set(true);
            }
            putRelationship(method, target, top);
            optional.setAClass(target.getClassReturnType());
            optional.setAnInterface(target.getInterfaceReturnType());
        }
    }

    private void putRelationship(Method method, Method target, MethodCallExpr top) {
        Stack<State> remains = new Stack<>();

        while(!states.isEmpty()) {
            State state = states.poll();
            if(state.getCmd().equals("POP")) {
                if(remains.isEmpty() || remains.peek().getCmd().equals("POP"))
                    remains.push(state);
                else // PUSH in the stack
                    remains.pop();
            } else remains.push(state);
        }

        List<State> stateList = new ArrayList<>(remains);

        method.getInvokes().add(
                new MethodRelationship(
                        order.postInc(),
                        stateList,
                        top.getArguments().stream()
                                .map(Expression::toString)
                                .collect(Collectors.toList()),
                        target
                )
        );
    }

    private void processArguments(List<Expression> arguments, Method method) {
        arguments.forEach(argument -> {
            argument.findAll(MethodCallExpr.class).forEach(expr -> processMethodCall(expr, method));
        });
    }

    private void processConditional(IfStmt ifStmt, Method method) {
        Long parentId = id.getNum();

        IfStmt iter = ifStmt;
        //IfStmt는 3개의 children으로 이뤄짐
        // 1. 현재 if의 condition
        // 2. 현재 if의 body
        // 3. 다음 if or else
        while(!iter.getChildNodes().isEmpty()) {
            List<Node> children = iter.getChildNodes();
            String condition = children.get(0).toString();
            // if parentId == id, then root if
            // else, else if
            process(children.get(1),
                    new State("PUSH", "If", condition, id.postInc(), parentId),
                    method);

            if(children.size() == 2) break;
            else if (children.get(2) instanceof IfStmt) iter = (IfStmt) children.get(2);
            else { // children.get(2) instanceof Block
                process(children.get(2),
                        new State("PUSH", "Else", "", id.postInc(), parentId),
                        method);
                break;
            }
        }
    }

    private void process(Node node, State state, Method method) {
        states.add(state);
        traverse(node, method);
        states.add(new State("POP"));
    }


    // 이름만 같으면 메소드 오버로딩이 있더라도 리턴타입은 같음.
    private Method getMethod(String key, String methodName) {
        String signature = key + "." + methodName;
        var map =
                mapper.methods.get(key).subMap(signature, signature+Character.MAX_VALUE);
        return map.isEmpty()? null: map.get(map.firstKey());
    }

    @Getter
    @Setter
    private static class Container {
        private Long num;
        Container(Long num) {
            this.num = num;
        }

        public Long postInc() {
            num = num + 1;
            return num - 1;
        }
    }
}
