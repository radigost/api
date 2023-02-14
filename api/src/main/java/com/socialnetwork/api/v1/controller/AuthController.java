package com.socialnetwork.api.v1.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import com.socialnetwork.api.service.AuthService;
import com.socialnetwork.api.service.UserService;
import com.socialnetwork.api.v1.domain.AuthData;
import com.socialnetwork.api.v1.domain.UserDto;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private AuthService authService;

    private AuthenticationManager authenticationManager;

    private UserService userService;

    @GetMapping("me")
    public UserDto getMyAccId(Principal principal){
        return userService.getUserIdByUsername(principal.getName());
    }

    @PostMapping(value = "login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate(@RequestBody AuthData authData) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authData.getUsername(), authData.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok("Login successfull");
    }

    @PostMapping(value = "register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody AuthData authData) {
        var res = authService.register(authData);
        if (res) {
            return ResponseEntity.ok("User created");
        }
        return ResponseEntity.badRequest().build();
    }

}

