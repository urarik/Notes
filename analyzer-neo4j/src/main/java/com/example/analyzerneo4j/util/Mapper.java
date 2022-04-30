package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.entity.*;
import com.example.analyzerneo4j.entity.Class;
import com.example.analyzerneo4j.entity.Package;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Mapper {
    public final Map<String, Package> packages = new HashMap<>();
    public final Map<String, Class> classes = new HashMap<>();
    public final Map<String, Interface> interfaces = new HashMap<>();
    public final Map<String, SortedMap<String, Method>> methods = new HashMap<>(); //path+class -> signature -> method
    public final Map<String, Map<String, Member>> members = new HashMap<>(); //path+class -> name -> member
    public final Map<Method, Map<String, Parameter>> parameters = new HashMap<>();

    // block으로 막지말고 동시 저장이 되게 하려면?
    // => 각 단계별 다른 노드와의 의존성을 없애야 함
    public void store(EntityContainer entityContainer) {
            entityContainer.packageRepository.saveAll(packages.values()).collectList().block();
            entityContainer.classRepository.saveAll(classes.values()).collectList().block();
            entityContainer.interfaceRepository.saveAll(interfaces.values()).collectList().block();
            entityContainer.methodRepository.saveAll(methods.values().stream()
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())).collectList().block();
            entityContainer.memberRepository.saveAll(members.values().stream()
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())).collectList().block();
            entityContainer.parameterRepository.saveAll(parameters.values().stream()
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())).collectList().block();
    }
}
