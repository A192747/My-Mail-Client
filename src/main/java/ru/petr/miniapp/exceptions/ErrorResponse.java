package ru.petr.miniapp.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private String message;
    private Date timestamp;
}
