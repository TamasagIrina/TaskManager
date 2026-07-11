package org.example.tasks.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TaskFilterDTO {

    private String taskName;
    private String statusName;
    private String username;
    private LocalDateTime dueDateTime;
}
