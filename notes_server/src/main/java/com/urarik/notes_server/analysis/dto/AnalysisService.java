package com.urarik.notes_server.analysis.dto;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AnalysisService {
    private final WebClient webClient = WebClient.create("http://localhost:8888");


}
