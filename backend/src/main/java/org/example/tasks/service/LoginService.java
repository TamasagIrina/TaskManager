package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.eclipse.jetty.util.security.Credential;
import org.example.tasks.dto.request.AuthRequest;
import org.example.tasks.model.User;
import org.example.tasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    private final JwtService jwtService;
    public ResponseEntity<String> login(AuthRequest authRequest) throws JoseException {
        authRequest.setEmail(new String(Base64.getDecoder().decode(authRequest.getEmail())));
        authRequest.setPassword(new String(Base64.getDecoder().decode(authRequest.getPassword())));

        String hashPassword = Credential.MD5.digest(authRequest.getPassword()).replaceFirst("MD5:", "").toLowerCase();

        User dbUser = userRepository.findByEmail(authRequest.getEmail());

        if (dbUser == null) {
            return new ResponseEntity<>("401: Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        if (!hashPassword.equals(dbUser.getPassword())) {
            return new ResponseEntity<>("401: Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.createToken(authRequest.getEmail());

        if (token == null || token.isBlank()) {
            return new ResponseEntity<>("500: Empty response", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(token, HttpStatus.OK);
    }


}
