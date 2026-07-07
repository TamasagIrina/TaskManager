package org.example.tasks.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    private String content;

    @NotBlank()
    @Pattern(regexp = "TODO|IN_PROGRESS|DONE")
    private String status;

    @Future()
    private LocalDateTime dueDateTime;
}
