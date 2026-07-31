package org.example.tasks.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCreateDTO {

    @NotBlank()
    @Size(max = 500)
    private String projectName;

    @Size(max = 4000)
    private String projectDescription;

    private String statusTypeId;

    private List<Long> memberIds;
}
