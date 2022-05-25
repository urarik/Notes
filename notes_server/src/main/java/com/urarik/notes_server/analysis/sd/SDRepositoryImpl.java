package com.urarik.notes_server.analysis.sd;

import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Repository
public class SDRepositoryImpl implements SDRepository {
    private final WebClient webClient = WebClient.create("http://localhost:8888/analyze");

    @Override
    public List<Map<String, Object>> getInvokes(Long pid, Long mid) {
        Object response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sd")
                        .queryParam("pid", pid)
                        .queryParam("mid", mid)
                        .build())
                .retrieve().bodyToMono(Object.class).block();
        return (List<Map<String, Object>>) response;
    }

    @Override
    public List<Map<String, Object>> getBefore(Long pid, Long mid) {
        Object response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sd/before")
                        .queryParam("pid", pid)
                        .queryParam("mid", mid)
                        .build())
                .retrieve().bodyToMono(Object.class).block();
        return (List<Map<String, Object>>) response;
    }
}
