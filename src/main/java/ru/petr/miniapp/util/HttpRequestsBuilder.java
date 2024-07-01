package ru.petr.miniapp.util;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

@Component
public class HttpRequestsBuilder {
    public HttpRequest postHttpAuthRequest(String url, String body) throws URISyntaxException {
         return HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "ru")
                .header("Cache-Control", "no-cache")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "same-site")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Origin", "https://adfs.spbstu.ru")
                .header("Referer", "https://adfs.spbstu.ru/")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Gpc", "1")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0")
                .build();
    }

    public HttpRequest postHttpRequest(String url, String body) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "ru")
                .header("Cache-Control", "no-cache")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "same-site")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Gpc", "1")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0")
                .build();
    }

    public HttpRequest getHttpRequest(String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();
    }
}
