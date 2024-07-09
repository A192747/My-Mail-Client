package ru.petr.miniapp.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.petr.miniapp.service.refresh.Refreshable;
import ru.petr.miniapp.util.HttpRequestsBuilder;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    @Value("${mail.url}")
    private String MAIL_URL;
    private final HttpRequestsBuilder httpRequestsBuilder;
    private final RestTemplate restTemplate;
    private final HttpClient httpClient;
    private final Refreshable refreshable;



    public boolean isAbleToLogin(String mail, String pass) {
        try {
            String page = refreshable.refresh(login(mail, pass));
            return isMailPage(page);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isMailPage(String page) {
        Document document = Jsoup.parse(page);
        return document.head().getElementsByTag("title").stream().anyMatch(element -> element.toString().contains("Входящие - Outlook"));
    }
    @SneakyThrows
    public String login(String mail, String pass) {
        String loginUrl = getLoginUrl();

        String authPage = getAuthPage(loginUrl, mail, pass);

        return finalLoginUrl(authPage);
    }


    private String getLoginUrl() {
        String body = restTemplate.getForEntity(MAIL_URL, String.class).getBody();
        Document doc = Jsoup.parse(body);
        Element form = doc.getElementById("options");
        String actionUrl = form.attr("action");
        return actionUrl;
    }

    private String getAuthPage(String url, String mail, String pass) throws URISyntaxException, IOException, InterruptedException {
        String requestBody = "UserName=" + mail + "&Password=" + pass + "&AuthMethod=FormsAuthentication";
        httpClient.send(httpRequestsBuilder.postHttpAuthRequest(url, requestBody), HttpResponse.BodyHandlers.ofString());
        return String.valueOf(httpClient.send(httpRequestsBuilder.getHttpRequest(url), HttpResponse.BodyHandlers.ofString()).body());
    }

    private String finalLoginUrl(String authPage) throws URISyntaxException, IOException, InterruptedException {
        Document doc = Jsoup.parse(authPage);
        String url = doc.getElementsByAttribute("method").attr("action");

        List<Element> els = doc.getElementsByAttribute("value");

        els.remove(els.size() - 1);

        StringBuilder str = new StringBuilder();
        boolean isFirstTime = true;
        for (Element el : els) {
            if (!isFirstTime) {
                str.append("&");
            }
            isFirstTime = false;
            str.append(URLEncoder.encode(el.attributes().get("name"), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(el.attributes().get("value"), StandardCharsets.UTF_8));

        }

        //POST
        httpClient.send(httpRequestsBuilder.postHttpAuthRequest(url, String.valueOf(str)), HttpResponse.BodyHandlers.ofString());

        return url;
    }

}
