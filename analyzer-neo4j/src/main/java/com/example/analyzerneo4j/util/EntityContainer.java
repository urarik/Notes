package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.repository.*;
import org.springframework.stereotype.Component;

@Component
public class EntityContainer {
    public final ClassRepository classRepository;
    public final InterfaceRepository interfaceRepository;
    public final MemberRepository memberRepository;
    public final MethodRepository methodRepository;
    public final PackageRepository packageRepository;
    public final ParameterRepository parameterRepository;
    public final ProjectRepository projectRepository;

    public EntityContainer(ClassRepository classRepository, InterfaceRepository interfaceRepository, MemberRepository memberRepository, MethodRepository methodRepository, PackageRepository packageRepository, ParameterRepository parameterRepository, ProjectRepository projectRepository) {
        this.classRepository = classRepository;
        this.interfaceRepository = interfaceRepository;
        this.memberRepository = memberRepository;
        this.methodRepository = methodRepository;
        this.packageRepository = packageRepository;
        this.parameterRepository = parameterRepository;
        this.projectRepository = projectRepository;
    }
}
