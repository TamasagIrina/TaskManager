package org.example.tasks.controller;

import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.service.RegisterService;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@CrossOrigin
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserCreateDTO user) throws JoseException {

        String response= registerService.register(user);

        if (response == null || response.isBlank()) {
            return new ResponseEntity<>("500: Empty response",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response.startsWith("409:")) {
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }

        if (response.startsWith("500:")) {
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
