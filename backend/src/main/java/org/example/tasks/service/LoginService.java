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
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    private final JwtService jwtService;


    public String login(AuthRequest authRequest) throws JoseException {
        authRequest.setEmail(new String(Base64.getDecoder().decode(authRequest.getEmail())));
        authRequest.setPassword(new String(Base64.getDecoder().decode(authRequest.getPassword())));

        String hashPassword = Credential.MD5.digest(authRequest.getPassword()).replaceFirst("MD5:", "").toLowerCase();

        User dbPassword= userRepository.findByEmail(authRequest.getEmail());

        if(hashPassword.equals(dbPassword.getPassword())){
            return jwtService.createToken(authRequest.getEmail());
        }else {
            return "401: Unauthorized";
        }

    }


}
