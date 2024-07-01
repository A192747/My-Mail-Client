package ru.petr.miniapp.broker;

import lombok.SneakyThrows;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class InMemoryBroker {
    private final BlockingDeque<String> mailingQueue= new LinkedBlockingDeque<>();

    public void publish(String message) {
        mailingQueue.offer(message);
    }

    @SneakyThrows
    public String take() {
        return mailingQueue.take();
    }
}
