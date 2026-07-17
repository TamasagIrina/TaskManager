package org.example.tasks.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private LocalDate birthDate;
    private Boolean isInternal;
    private String createdBy;
    private LocalDateTime creationDate;
}
