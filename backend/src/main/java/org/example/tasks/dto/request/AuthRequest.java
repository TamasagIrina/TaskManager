package org.example.tasks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String password;

}
