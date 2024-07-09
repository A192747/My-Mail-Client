package ru.petr.miniapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.petr.miniapp.config.DefaultUserInitializer;
import ru.petr.miniapp.model.User;
import ru.petr.miniapp.model.UserMails;
import ru.petr.miniapp.repository.UserRepository;
import ru.petr.miniapp.service.refresh.Refreshable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailСheckingService {

    private final Publisher<UserMails> publisher;
    private final UserRepository<User> repository;
    private final LoginService loginService;
    private final MailParserService mailParserService;

    private final Refreshable refreshable;
    private static User defaultUser = DefaultUserInitializer.defaultUser;
    private static String defaultUserPage = DefaultUserInitializer.defaultPage;


    @Scheduled(fixedRateString = "${mail.recheckperiod.mins:1440}", timeUnit = TimeUnit.MINUTES)
    public void checkMail() {
        if(defaultUser != null && defaultUserPage != null) {
            defaultUserPage = refreshable.refresh();
            log.info("Refresh default user mail list");
            for(int i = 0; i < 2; i++) {
                if(loginService.isMailPage(defaultUserPage)) {
                    check(defaultUser, defaultUserPage);
                    log.info("Send message to default user");
                    break;
                } else {
                    defaultUserPage = refreshable.refresh(
                            loginService.login(
                                    defaultUser.getMail(),
                                    defaultUser.getPass()
                            )
                    );
                    log.warn("Try to login default user");
                }

            }
        } else {
            User user;
            while ((user = repository.getNext()) != null) {
                String url = loginService.login(user.getMail(), user.getPass());

                String page = refreshable.refresh(url);

                if (page != null) {
                    check(user, page);
                } else {
                    log.error("User: " + user.getTelegramNumber() + " can`t login!");
                }
            }
        }
    }

    private void check(User user, String page) {
        UserMails messages = mailParserService.getNewMails(user.getLastMessageDate(), page);

        if (messages != null && !messages.getUserMessages().isEmpty()) {
            messages.setUserId(user.getTelegramNumber());
            publisher.publish(messages);
        }
    }
}
