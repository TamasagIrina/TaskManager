package org.example.tasks.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TaskDTO {

    private Long taskId;
    private String taskName;

    private String statusTypeId;
    private String statusName;

    private Long userId;
    private String username;

    private LocalDate dueDate;
    private LocalDateTime creationDate;
    private String createdBy;
    private LocalDateTime lastUpdateDate;

}
