package com.example.analyzerneo4j.util;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class Parser {
    private final RestTemplate restTemplate = new RestTemplate();

    public String getCode(String urlString) throws URISyntaxException {
        URI uri = new URI(urlString);
        ResponseEntity<String> code = restTemplate.getForEntity(uri, String.class);
        //TODO status에 따른 에러 처리
        return code.getBody();
    }

    public Document getDocument(String urlString) throws IOException, URISyntaxException {
//        OkHttpClient okHttp = new OkHttpClient();
//        Request request = new Request.Builder().url(url).get().build();
        URI uri = new URI(urlString);
        String body = restTemplate.getForEntity(uri, String.class).getBody();

        assert body != null;

        return Jsoup.parse(body);
    }

}
