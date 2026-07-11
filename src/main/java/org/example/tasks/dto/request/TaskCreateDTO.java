package org.example.tasks.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Future
    private LocalDate dueDate;
}
