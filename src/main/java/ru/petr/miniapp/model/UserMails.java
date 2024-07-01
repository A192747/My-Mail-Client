package ru.petr.miniapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class UserMails {
    private Long userId;
    private List<MailMessage> userMessages;
    public UserMails(@JsonProperty("userId") Long userId,
                     @JsonProperty("userMessages") List<MailMessage> userMessages) {
        this.userId = userId;
        this.userMessages = new ArrayList<>(userMessages);
    }

    public UserMails() {

    }
}
