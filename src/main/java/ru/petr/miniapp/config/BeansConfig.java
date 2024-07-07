package ru.petr.miniapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import ru.petr.miniapp.broker.InMemoryBroker;
import ru.petr.miniapp.broker.PostProcessor;
import ru.petr.miniapp.service.Publisher;
import ru.petr.miniapp.broker.SubsContainer;
import ru.petr.miniapp.model.UserMails;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;

@Configuration
@EnableScheduling
public class BeansConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CookieManager cookieManager() {
        return new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    }

    @Bean
    public HttpClient httpClient(CookieManager cookieManager) {
        return HttpClient.newBuilder()
                .cookieHandler(cookieManager) // Настройка CookieManager
                .build();
    }

    @Bean
    public SubsContainer subsContainer(InMemoryBroker inMemoryBroker, ObjectMapper objectMapper) {
        return new SubsContainer(objectMapper, inMemoryBroker);
    }

    @Bean
    public PostProcessor postProcessor() {
        return new PostProcessor();
    }

    @Bean
    public InMemoryBroker inMemoryBroker() {
        return new InMemoryBroker();
    }

    @Bean
    public Publisher<UserMails> publisher(InMemoryBroker inMemoryBroker, ObjectMapper objectMapper) {
        return new Publisher<>(inMemoryBroker, objectMapper);
    }

}
