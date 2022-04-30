package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.entity.Interface;
import com.example.analyzerneo4j.entity.Class;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerOptional {
    private Class aClass;
    private Interface anInterface;

    public ContainerOptional(Class aClass, Interface anInterface) {
        this.aClass = aClass;
        this.anInterface = anInterface;
    }

    public boolean empty() {
        return aClass == null && anInterface == null;
    }
    public boolean present() {
        return !empty();
    }

    public boolean isClass() {
        return aClass != null;
    }
    public boolean isInterface() {
        return anInterface != null;
    }
}
