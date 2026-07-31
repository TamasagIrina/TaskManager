package org.example.tasks.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private String statusTypeId;
    private String statusName;
    private String createdBy;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
    private List<Long> memberIds;
}
