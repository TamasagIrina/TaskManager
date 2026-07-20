package org.example.tasks.controller;

import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.AuthRequest;
import org.example.tasks.service.LoginRegisterService;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@CrossOrigin
public class LoginController {
    private final LoginRegisterService loginRegisterService;

    @PostMapping()
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) throws JoseException {
        String response= loginRegisterService.login(authRequest);
        if(response.equals("401: Unauthorized")) {
            return new ResponseEntity<>("401: Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
