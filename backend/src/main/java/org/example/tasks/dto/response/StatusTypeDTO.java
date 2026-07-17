package org.example.tasks.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatusTypeDTO {
    private String statusTypeId;
    private String statusName;
    private String createdBy;
    private LocalDateTime creationDate;
}
