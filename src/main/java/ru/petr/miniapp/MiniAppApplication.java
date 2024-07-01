package ru.petr.miniapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class MiniAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniAppApplication.class, args);
    }

}
