package ru.petr.miniapp.service.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.petr.miniapp.bot.TelegramBot;
import ru.petr.miniapp.config.DefaultUserInitializer;
import ru.petr.miniapp.model.UserMails;
import ru.petr.miniapp.repository.MyUserRepository;

import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class MailingSender implements Sender<UserMails> {
    private final TelegramBot bot;
    private final MyUserRepository repository;
    @Override
    public void send(UserMails object) {
        Date currentDate = Date.from(Instant.now());
        repository.setDateForUserId(currentDate, object.getUserId());
        bot.sendUserMails(object);
    }
}
