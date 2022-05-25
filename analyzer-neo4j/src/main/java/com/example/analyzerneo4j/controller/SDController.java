package com.example.analyzerneo4j.controller;

import com.example.analyzerneo4j.service.SDService;
import org.apache.coyote.Response;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analyze/sd")
public class SDController {
    private final SDService sdService;

    public SDController(SDService sdService) {
        this.sdService = sdService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getInvokes(
            @RequestParam Long pid,
            @RequestParam Long mid
    ) {
        List<Map<String, Object>> res = sdService.getInvokes(pid, mid);

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/before")
    public ResponseEntity<List<Map<String, Object>>> getBefore(
            @RequestParam Long pid,
            @RequestParam Long mid
    ) {
        List<Map<String, Object>> res = sdService.getBefore(pid, mid);
        return ResponseEntity.ok(res);
    }
}
