package com.crudtest.controller;

import com.crudtest.request.EmailCreate;
import com.crudtest.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/email")
    public Long email(@RequestBody EmailCreate request) {
        request.validate();
        return emailService.emailWrite(request);
    }
}
