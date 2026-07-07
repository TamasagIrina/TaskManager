package org.example.tasks.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TaskDTO {

    private Long id;
    private String content;
    private String status;
    private LocalDateTime dueDateTime;

}
