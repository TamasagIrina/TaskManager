package org.example.tasks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {
    @NotBlank()
    @Size(max = 500)
    private String username;

    @NotNull()
    @Past()
    private LocalDate birthDate;

    @NotNull()
    private Boolean isInternal;
}
