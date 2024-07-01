package ru.petr.miniapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.petr.miniapp.service.MailСheckingService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class MethodsController {

    private final MailСheckingService invokeService;


    @PostMapping
    public ResponseEntity<String> startAllMethods() throws IOException, URISyntaxException, ParseException, InterruptedException {
        invokeService.checkMail();
        return ResponseEntity.ok("asdf");
    }


}
