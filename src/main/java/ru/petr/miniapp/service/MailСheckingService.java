package ru.petr.miniapp.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.petr.miniapp.model.UserMails;
import ru.petr.miniapp.model.User;
import ru.petr.miniapp.repository.UserRepository;

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


    @Scheduled(fixedRateString = "${mail.recheckperiod.mins:1440}", timeUnit = TimeUnit.MINUTES)
    public void checkMail() throws URISyntaxException, IOException, InterruptedException, ParseException {
        User user;
        while ((user = repository.getNext()) != null) {
            String page = loginService.login(user.getMail(), user.getPass());

            UserMails messages = mailParserService.getNewMails(user.getLastMessageDate(), page);

            if(messages != null) {
                messages.setUserId(user.getTelegramNumber());
                publisher.publish(messages);
            }
        }
    }
}
