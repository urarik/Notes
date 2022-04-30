package com.example.analyzerneo4j.util;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

public enum NodeType {
    INSTANCE, BLOCK, METHOD_CALL, CONDITIONAL, LOOP, OTHER;

    public NodeType getType(Node node) {
        if(node instanceof BlockStmt) return BLOCK;
        if(node instanceof MethodCallExpr) return METHOD_CALL;
        if(node instanceof IfStmt) return CONDITIONAL;
        if(node instanceof WhileStmt || node instanceof ForEachStmt || node instanceof ForStmt) return LOOP;
        else return OTHER;
    }
}
