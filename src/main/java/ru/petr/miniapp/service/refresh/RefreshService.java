package ru.petr.miniapp.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.petr.miniapp.util.HttpRequestsBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshService implements Refreshable {
    private final HttpClient httpClient;
    private final HttpRequestsBuilder httpRequestsBuilder;

    @Override
    @SneakyThrows
    public String refresh(String url) {
        //GET
        HttpResponse<String> response = httpClient.send(httpRequestsBuilder.getHttpRequest(url), HttpResponse.BodyHandlers.ofString());

        //Если не удалось зайти с первого раза. Попробуем ещё 2 раза
        for (int i = 0; i < 2; i++) {
            if (response.statusCode() == 200) {
                log.info("Count of retries to refresh: {}", i);
                return response.body();
            } else {
                response = httpClient.send(httpRequestsBuilder.getHttpRequest(url), HttpResponse.BodyHandlers.ofString());
            }
        }
        log.error("Count of retries to login was too much");
        return null;
    }
    @Override
    public String refresh() {
        return refresh("https://mail.spbstu.ru:443/owa/");
    }
}
