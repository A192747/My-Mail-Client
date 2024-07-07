package ru.petr.miniapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.petr.miniapp.bot.TelegramBot;
import ru.petr.miniapp.broker.Sub;
import ru.petr.miniapp.model.UserMails;
import ru.petr.miniapp.repository.MyUserRepository;
import ru.petr.miniapp.service.sender.Sender;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailingSubscriber {

    private final Sender<UserMails> sender;
    @Sub
    public void subscriber(UserMails message) {
        sender.send(message);
//        log.info("Input message {}", message);
    }
}
