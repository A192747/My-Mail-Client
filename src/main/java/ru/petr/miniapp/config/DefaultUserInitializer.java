package ru.petr.miniapp.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.petr.miniapp.model.User;
import ru.petr.miniapp.repository.UserRepository;
import ru.petr.miniapp.service.LoginService;
import ru.petr.miniapp.service.refresh.Refreshable;

import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.Date;

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
    @Value("${default.user.only}")
    public boolean onlyOneUser;
    public static User defaultUser;
    public static String defaultPage;
    public final Refreshable refreshable;

    @PostConstruct
    public void init() {
        // Создаем defalt пользователя
        User defaultUserInit = new User();
        if(mail != null && pass != null && chatId != null) {
            defaultUserInit.setMail(mail);
            defaultUserInit.setPass(pass);
            defaultUserInit.setTelegramNumber(chatId);
            defaultUserInit.setLastMessageDate(Date.from(Instant.EPOCH));
            if(loginService.isAbleToLogin(mail, pass)) {
                // Сохраняем пользователя в базу данных
                if(onlyOneUser) {
                    defaultUser = defaultUserInit;
                    defaultPage = refreshable.refresh();
                }
                repository.save(defaultUserInit);
                log.info("Default user added");
                return;
            } else {
                log.error("Default user login error! Check entered email and password, or try to run later");
                System.exit(1);
            }
        }
        log.info("Default user is not added");
    }
}