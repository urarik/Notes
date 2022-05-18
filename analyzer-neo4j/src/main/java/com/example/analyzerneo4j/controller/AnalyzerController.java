package com.example.analyzerneo4j.controller;

import com.example.analyzerneo4j.controller.dto.AnalyzeRequest;
import com.example.analyzerneo4j.service.AnalyzerService;
import org.apache.coyote.Response;
import org.json.JSONObject;
import org.neo4j.driver.types.Node;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analyze")
public class AnalyzerController {
    AnalyzerService analyzerService;

    public AnalyzerController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping
    public ResponseEntity<Object> analyze(@RequestBody AnalyzeRequest request) throws IOException, URISyntaxException {
        try {
            analyzerService.process(request.getLink(), request.getPid());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/class")
    public ResponseEntity<String> getEntityForClassDiagram(
            @RequestParam Long pid,
            @RequestParam Long cid,
            @RequestParam Long depth) {
        var result = analyzerService.getEntityForClassDiagram(pid, cid, depth);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/package")
    public ResponseEntity<String> getPackages(
            @RequestParam Long pid,
            @RequestParam Long order) {
        var result = analyzerService.getPackages(pid, order);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/entities")
    public ResponseEntity<String> getEntitiesByPackage(
            @RequestParam Long pid,
            @RequestParam Long packageId) {
        var result = analyzerService.getClassAndInterface(pid, packageId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/entity")
    public ResponseEntity<Map<Long, String>> getEntityInIds(
            @RequestParam List<Long> eids,
            @RequestParam Long pid
    ) {
        return ResponseEntity.ok(analyzerService.findEntityInIds(eids, pid));
    }

    @GetMapping("/methods")
    public ResponseEntity<Map<Long, List<String>>> getMethods(
            @RequestParam List<Long> cids,
            @RequestParam Long pid
    ) {
        return ResponseEntity.ok(analyzerService.findMethodListInIds(cids, pid));
    }

    @GetMapping("/members")
    public ResponseEntity<Map<Long, List<String>>> getMembers(
            @RequestParam List<Long> cids,
            @RequestParam Long pid
    ) {
        return ResponseEntity.ok(analyzerService.findMemberListInIds(cids, pid));
    }
}
