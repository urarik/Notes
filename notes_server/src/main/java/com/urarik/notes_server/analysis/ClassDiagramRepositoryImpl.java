package com.urarik.notes_server.analysis;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ClassDiagramRepositoryImpl implements ClassDiagramRepository {
    private final WebClient webClient = WebClient.create("http://localhost:8888");

    @Override
    public Map<String, List<JSONObject>> findMethodMapInIds(List<Long> cidList, Long pid) {
        Object response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/analyze/methods")
                        .queryParam("pid", pid)
                        .queryParam("cids", cidList)
                        .build())
                .retrieve().bodyToMono(Object.class).block();
        return getJsonMap((Map<String, List<String>>) response);
    }

    @Override
    public Map<String, List<JSONObject>> findMemberMapInIds(List<Long> cidList, Long pid) {
        Object response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/analyze/members")
                        .queryParam("pid", pid)
                        .queryParam("cids", cidList)
                        .build())
                .retrieve().bodyToMono(Object.class).block();
        return getJsonMap((Map<String, List<String>>) response);
    }

    @Override
    public Map<String, JSONObject> findEntityMapInIds(List<Long> eidList, Long pid) {
        Object response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/analyze/entity")
                        .queryParam("pid", pid)
                        .queryParam("eids", eidList)
                        .build())
                .retrieve().bodyToMono(Object.class).block();
        Map<String, JSONObject> map = new HashMap<>();
        ((Map<String, String>)response).forEach((key, value) -> map.put(key, new JSONObject(value)));

        return map;
    }

    @NotNull
    private Map<String, List<JSONObject>> getJsonMap(Map<String, List<String>> response) {
        Map<String, List<String>> map = response;
        Map<String, List<JSONObject>> ret = new HashMap<>();

        map.forEach((key, value) -> {
            ret.put(key, value.stream().map(JSONObject::new).collect(Collectors.toList()));
        });

        return ret;
    }

//    @Override
//    public JSONObject findEntityById(Long eid, Long pid) {
//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/analyze/entity")
//                        .queryParam("pid", pid)
//                        .queryParam("eid", eid)
//                        .build())
//                .retrieve().bodyToMono(JSONObject.class).block();
//    }


}
