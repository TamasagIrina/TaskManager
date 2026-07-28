package org.example.tasks.security;

import lombok.RequiredArgsConstructor;
import org.example.tasks.service.CurrentUserService;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    private final CurrentUserService currentUserService;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.of(currentUserService.getCurrentUser().getEmail());
        } catch (IllegalStateException e) {
            return Optional.of("SYSTEM");
        }
    }
}
