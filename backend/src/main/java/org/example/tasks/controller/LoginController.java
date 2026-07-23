package org.example.tasks.controller;

import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.AuthRequest;
import org.example.tasks.service.LoginService;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@CrossOrigin
public class LoginController {
    private final LoginService loginService;

    @PostMapping()
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) throws JoseException {
        return loginService.login(authRequest);
    }
}
