package ru.petr.miniapp.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.petr.miniapp.model.User;
import ru.petr.miniapp.repository.UserRepository;
import ru.petr.miniapp.service.LoginService;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUserInitializer {
    private final UserRepository<User> repository;
    private final LoginService loginService;

    @Value("${default.user.mail}")
    private String mail;
    @Value("${default.user.password}")
    private String pass;
    @Value("${default.user.chatId}")
    private Long chatId;

    @PostConstruct
    public void init() {
        // Создаем defalt пользователя
        User defaultUser = new User();
        if(mail != null && pass != null && chatId != null) {
            defaultUser.setMail(mail);
            defaultUser.setPass(pass);
            defaultUser.setTelegramNumber(chatId);
            defaultUser.setLastMessageDate(Date.from(Instant.now()));

            if(loginService.testLogin(mail, pass)) {
                // Сохраняем пользователя в базу данных
                repository.save(defaultUser);
                log.info("Default user added");
                return;
            } else {
                log.error("Default user login error! Check that the entered email and password are correct");
                System.exit(1);
            }
        }
        log.info("Default user is not added");
    }
}