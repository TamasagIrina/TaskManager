package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.util.security.Credential;
import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.mapper.UserMapper;
import org.example.tasks.model.User;
import org.example.tasks.repository.UserRepository;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    public ResponseEntity<String> register(UserCreateDTO userCreateDTO) throws JoseException {
        String email = new String(Base64.getDecoder().decode(userCreateDTO.getEmail()));
        String password = new String(Base64.getDecoder().decode(userCreateDTO.getPassword()));

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("403: Empty response", HttpStatus.FORBIDDEN);
        }

        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            return new ResponseEntity<>("403: Email already in use", HttpStatus.FORBIDDEN);
        }

        String hashPassword = Credential.MD5.digest(password).replaceFirst("MD5:", "").toLowerCase();
        userCreateDTO.setPassword(hashPassword);
        userCreateDTO.setEmail(email);

        User newUser = userMapper.toEntity(userCreateDTO);

        User savedUser;
        try {
            savedUser = userRepository.save(newUser);
        } catch (Exception e) {
            return new ResponseEntity<>("500: Failed to save user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (savedUser.getUserId() == null || savedUser.getUserId() == 0) {
            return new ResponseEntity<>("500: Failed to save user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String token = jwtService.createToken(email);

        if (token == null || token.isBlank()) {
            return new ResponseEntity<>("500: Empty response", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(token, HttpStatus.OK);
    }



}
