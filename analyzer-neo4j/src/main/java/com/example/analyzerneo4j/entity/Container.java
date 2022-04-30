package com.example.analyzerneo4j.entity;

import java.util.Set;

public interface Container {
    public Package getAPackage();
    public void setAPackage(Package aPackage);

//    public Set<ContainerRelationship> getRelates();
//    public void setRelates(Set<ContainerRelationship> set);
//
//    public Set<ContainerRelationship> getNested();
//    public void setNested(Set<ContainerRelationship> set);
}
