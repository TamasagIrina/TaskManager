package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.util.security.Credential;
import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.mapper.UserMapper;
import org.example.tasks.model.User;
import org.example.tasks.repository.UserRepository;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    public String register(UserCreateDTO userCreateDTO) throws JoseException {
        String email = new String(Base64.getDecoder().decode(userCreateDTO.getEmail()));
        String password = new String(Base64.getDecoder().decode(userCreateDTO.getPassword()));

        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            return "409: Email already in use";
        }

        String hashPassword = Credential.MD5.digest(password).replaceFirst("MD5:", "").toLowerCase();
        userCreateDTO.setPassword(hashPassword);
        userCreateDTO.setEmail(email);

        User newUser = userMapper.toEntity(userCreateDTO);

        User savedUser;
        try {
            savedUser = userRepository.save(newUser);
        } catch (Exception e) {
            return "500: Failed to save user";
        }

        if (savedUser.getUserId() == null || savedUser.getUserId() == 0) {
            return "500: Failed to save user";
        }

        return jwtService.createToken(email);
    }



}
