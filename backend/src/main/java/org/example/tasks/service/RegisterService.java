package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.util.security.Credential;
import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.mapper.UserMapper;
import org.example.tasks.model.Role;
import org.example.tasks.model.User;
import org.example.tasks.repository.RoleRepository;
import org.example.tasks.repository.UserRepository;
import org.example.tasks.security.JwtService;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    private final  EmailService emailService;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    public ResponseEntity<String> register(UserCreateDTO userCreateDTO) throws JoseException {
        String email = new String(Base64.getDecoder().decode(userCreateDTO.getEmail()));
        String password = new String(Base64.getDecoder().decode(userCreateDTO.getPassword()));

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("409: Empty response", HttpStatus.CONFLICT);
        }

        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            return new ResponseEntity<>("409: Email already in use", HttpStatus.CONFLICT);
        }

        String hashPassword = Credential.MD5.digest(password).replaceFirst("MD5:", "").toLowerCase();
        userCreateDTO.setPassword(hashPassword);
        userCreateDTO.setEmail(email);

        User newUser = userMapper.toEntity(userCreateDTO);

        Role defaultRole = roleRepository.findByRoleName("USER");

        newUser.setRole(defaultRole);

        User savedUser;
        try {
            savedUser = userRepository.save(newUser);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("500: Failed to save user - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (savedUser.getUserId() == null || savedUser.getUserId() == 0) {
            return new ResponseEntity<>("500: Failed to save user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        emailService.sendConfirmationEmail(savedUser.getEmail(), savedUser.getUsername());
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<String> confirmAccount(String hashEmail) {
        String email = new String(Base64.getDecoder().decode(hashEmail));

        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            existingUser.setEmailConfirmed(true);
            userRepository.save(existingUser);
        }else {
            return new ResponseEntity<>("500: Failed to found user - " , HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("User email successfully confirmed", HttpStatus.CREATED);
    }



}
