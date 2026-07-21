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
        String response= loginService.login(authRequest);
        if(response.equals("401: Unauthorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (response.isBlank()) {
            return new ResponseEntity<>("500: Empty response",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
