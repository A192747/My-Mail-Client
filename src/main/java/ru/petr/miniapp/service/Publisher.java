package ru.petr.miniapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.petr.miniapp.broker.InMemoryBroker;

@RequiredArgsConstructor
public class Publisher <T>{

    private final InMemoryBroker inMemoryBroker;
    private final ObjectMapper objectMapper;
    @SneakyThrows
    public <T> void publish(T object) {
        inMemoryBroker.publish(objectMapper.writeValueAsString(object));
    }


}
