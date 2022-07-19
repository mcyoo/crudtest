package com.crudtest.controller;

import com.crudtest.request.UserCreate;
import com.crudtest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/email")
    public Map email(@RequestBody UserCreate request) {
        request.validate();
        Long userID = userService.emailWrite(request);
        return Map.of("userID",userID);
    }
}
