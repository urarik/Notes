package com.urarik.notes_server.analysis;

import com.urarik.notes_server.analysis.dto.AnalysisService;
import com.urarik.notes_server.analysis.dto.AnalyzeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/analyze")
public class AnalysisController {
    private final AnalysisService analysisService;
    private final WebClient webClient = WebClient.create("http://localhost:8888");


    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ResponseEntity<String> analyze(@RequestBody AnalyzeRequest request) {
        return webClient.post()
                .uri("/analyze")
                .bodyValue(request)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    @GetMapping("/class")
    public Mono<String> getEntityForClassDiagram(
            @RequestParam Long pid,
            @RequestParam Long cid,
            @RequestParam Long depth) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/analyze/class")
                        .queryParam("pid", pid)
                        .queryParam("cid", cid)
                        .queryParam("depth", depth)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/package")
    public Mono<String> getPackages(
            @RequestParam Long pid,
            @RequestParam Long order) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/analyze/package")
                        .queryParam("pid", pid)
                        .queryParam("order", order)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/entities")
    public Mono<String> getEntities(
            @RequestParam Long pid,
            @RequestParam Long packageId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/analyze/entities")
                        .queryParam("pid", pid)
                        .queryParam("packageId", packageId)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
