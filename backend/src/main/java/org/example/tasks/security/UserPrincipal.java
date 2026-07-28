package org.example.tasks.security;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class UserPrincipal {

    private final Long id;
    private final String email;
    private final String role;

}
