package com.socialnetwork.api.v1.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import com.socialnetwork.api.service.AuthService;
import com.socialnetwork.api.v1.domain.AuthData;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping(value = "login",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public boolean authenticate(@RequestBody AuthData authData) {
        return authService.authenticate(authData);
    }

    @PostMapping(value = "register",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public boolean register(@RequestBody AuthData authData) {
        System.out.printf(authData.toString());

        return authService.register(authData);
    }


}

