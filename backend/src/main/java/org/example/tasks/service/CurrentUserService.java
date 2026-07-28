package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import org.example.tasks.model.User;
import org.example.tasks.repository.UserRepository;
import org.example.tasks.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return userRepository.findById(principal.getId()).orElse(null);
        }
        throw new IllegalStateException("No authenticated user found");
    }
}
