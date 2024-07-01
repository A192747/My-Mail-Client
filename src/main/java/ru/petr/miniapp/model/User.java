package ru.petr.miniapp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@Data
public class User {
    private String mail;
    private String pass;
    private Long telegramNumber;
    private Date lastMessageDate;

    public User() {
    }
}
