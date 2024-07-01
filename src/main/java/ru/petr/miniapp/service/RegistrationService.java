package ru.petr.miniapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.petr.miniapp.model.User;
import ru.petr.miniapp.repository.MyUserRepository;
import ru.petr.miniapp.repository.UserRepository;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository<User> repository;

    public boolean register(Long userId, String mail, String pass, Date date) {
        User user = new User();
        user.setMail(mail);
        user.setPass(pass);
        user.setTelegramNumber(userId);
        user.setLastMessageDate(date);
        repository.save(user);
        return true;
    }

}
