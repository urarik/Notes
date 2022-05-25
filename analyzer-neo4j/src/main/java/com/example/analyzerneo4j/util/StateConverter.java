package com.example.analyzerneo4j.util;

import com.example.analyzerneo4j.entity.State;

import java.util.List;
import java.util.stream.Collectors;

public class StateConverter {
    public static List<String> convertStateToString(List<State> stateList) {
        return stateList.stream().map(state ->
                    state.getCmd() +
                    "|" +
                    state.getId() +
                    "|" +
                    state.getParentId() +
                    "|" +
                    state.getContent() +
                    "|" +
                    state.getType()
        ).collect(Collectors.toList());
    }
}
