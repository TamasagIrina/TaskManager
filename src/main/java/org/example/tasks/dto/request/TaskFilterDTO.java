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

    private String status;
    private String content;
    private LocalDateTime dueDateTime;
}
