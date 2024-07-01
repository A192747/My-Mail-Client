package ru.petr.miniapp.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.petr.miniapp.broker.PostProcessor.METHOD_2_BEEN;
@RequiredArgsConstructor
public class SubsContainer {
    private final ObjectMapper objectMapper;
    private final InMemoryBroker inMemoryBroker;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        METHOD_2_BEEN.entrySet().forEach(entry -> {
            executorService.submit(() -> {
                while (true) {
                    final var message = inMemoryBroker.take();
                    entry.getKey().invoke(
                            entry.getValue(),
                            objectMapper.readValue(message, entry.getKey().getParameterTypes()[0])
                    );
                }
            });
        });
    }
}
