package com.urarik.notes_server.analysis;

import com.urarik.notes_server.analysis.dto.AnalyzeRequest;
import com.urarik.notes_server.analysis.dto.PlaneWithName;
import com.urarik.notes_server.analysis.table.CDPlane;
import com.urarik.notes_server.security.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analyze")
public class AnalysisController {
    private final AnalysisService analysisService;
    private final WebClient webClient = WebClient.create("http://localhost:8888");
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserInfo userInfo;

    public AnalysisController(AnalysisService analysisService, UserInfo userInfo) {
        this.analysisService = analysisService;
        this.userInfo = userInfo;
    }

    @GetMapping("/code")
    public ResponseEntity<String> getCode(@RequestParam String url) throws URISyntaxException {
        URI uri = new URI(url);
        return ResponseEntity.ok(restTemplate.getForEntity(uri, String.class).getBody());
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

    @GetMapping("/classdiagram")
    public ResponseEntity<Map<Object, Object>> getClassDiagram(
            @RequestParam Long pid,
            @RequestParam Long planeId) throws IllegalAccessException {
        var temp = analysisService.getClassDiagram(pid, planeId, userInfo.getUsername());

        return ResponseEntity.ok(temp);
    }

    @PostMapping(value = "/classdiagram/save")
    public ResponseEntity<Object> createClassDiagram(@RequestBody CDPlane CDPlane) {
        analysisService.createPlane(CDPlane);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/classdiagram/saves")
    public ResponseEntity<List<PlaneWithName>> getPlaneList(
            @RequestParam Long pid
    ) {
        List<PlaneWithName> planeList = analysisService.getPlaneList(pid, userInfo.getUsername());

        return ResponseEntity.ok(planeList);
    }

}
