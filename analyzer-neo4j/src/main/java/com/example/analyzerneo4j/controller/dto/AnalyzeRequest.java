package com.example.analyzerneo4j.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyzeRequest {
    private String link;
    private Long pid;
}
