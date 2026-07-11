package org.example.tasks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatusTypeCreateDTO {
    @NotBlank()
    @Size(max = 500)
    private String statusName;
}
