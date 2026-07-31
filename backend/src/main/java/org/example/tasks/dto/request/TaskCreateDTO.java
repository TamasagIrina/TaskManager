package org.example.tasks.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TaskCreateDTO {

    @NotBlank()
    @Size(max = 500)
    private String taskName;

    @Size(max = 255)
    private String statusTypeId;

    private Long userId;

    @NotNull
    private Long projectId;

    @Future
    private LocalDate dueDate;
}
