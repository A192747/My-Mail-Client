package ru.petr.miniapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
public class MailMessage {
    private String author;
    private String title;
    private Date date;
    private String body;

    public MailMessage(@JsonProperty("author") String author,
                       @JsonProperty("title") String title,
                       @JsonProperty("date") Date date,
                       @JsonProperty("body") String body) {
        this.author = author;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public MailMessage() {

    }
}
