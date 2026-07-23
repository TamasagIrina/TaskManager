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

    @PostMapping()
    public ResponseEntity<String> register(@RequestBody UserCreateDTO userCreateDTO) throws JoseException {
        return registerService.register(userCreateDTO);
    }
}
