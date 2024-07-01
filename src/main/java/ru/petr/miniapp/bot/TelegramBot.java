package ru.petr.miniapp.bot;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.petr.miniapp.config.BotConfig;
import ru.petr.miniapp.model.MailMessage;
import ru.petr.miniapp.model.UserMails;
import ru.petr.miniapp.repository.MyUserRepository;
import ru.petr.miniapp.service.LoginService;
import ru.petr.miniapp.service.RegistrationService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    private final RegistrationService registrationService;
    private final MyUserRepository repository;
    private final LoginService loginService;

    @Override
    public String getBotUsername() { return config.getBotName(); }
    @Override
    public String getBotToken() { return config.getToken(); }
    @SneakyThrows
    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String memberName = update.getMessage().getFrom().getFirstName();


            if(messageText.contains("/login")) {
                if(repository.contains(chatId)) {
                    sendMessage(chatId, "Вы уже зарегистрированы!\n Если вы хотите сменить/удалить свои старые данные, то напишите в сообщении /delete");
                    return;
                }
                String[] arr = messageText.split("\\s");
                if(arr[0].equals("/login") && arr.length == 3) {
                    Date currentDate = Date.from(Instant.now());
                    if(loginService.testLogin(arr[1], arr[2])) {
                        registrationService.register(chatId, arr[1], arr[2], currentDate);
                        sendMessage(chatId, "Поздравляю! Вы успешно вошли в аккаунт");
                    }else {
                        sendMessage(chatId, "Ошибка! Не удалось войти в аккаунт");
                    }
                }
            } else {
                switch (messageText) {
                    case "/delete" -> {
                        if (repository.contains(chatId)) {
                            repository.remove(chatId);
                            sendMessage(chatId, "Данные о вас удалены");
                        } else {
                            sendMessage(chatId, "Нет сохраненных данных");
                        }
                    }
                    default -> startBot(chatId, memberName);
                }

            }
        }
    }

    private void startBot(long chatId, String userName) {
        sendMessage(chatId, "Привет, " + userName + "! Я телеграмм бот, который может отправлять тебе в телеграмме сообщения с твоей политеховской почты.\n" +
                "Давай знакомиться! Скорее пиши свои логин и пароль, и я смогу тебя уведомлять о новых сообщениях на твоей почте.\n " +
                "Пример:\n" +
                "/login example.ex@ex.example.com password");
    }

    private void sendMessage(long chatId, String messageStr) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageStr);
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    public void sendUserMails(UserMails mails) {
        Long userId = mails.getUserId();
        sendMessage(userId, "У вас на почте появились новые сообщения");
        for (MailMessage message: mails.getUserMessages()) {
            sendMessage(mails.getUserId(), formAnswer(message));
        }

    }

    private String formAnswer(MailMessage mailMessages) {
        StringBuilder builder = new StringBuilder();
        builder.append("От: ").append(mailMessages.getAuthor()).append("\n")
                .append("Тема: ").append(mailMessages.getTitle()).append("\n")
                .append("Время прихода: ").append(mailMessages.getDate()).append("\n")
                .append("Сообщение: \n").append(mailMessages.getBody()).append("\n");
        return String.valueOf(builder);

    }
}
