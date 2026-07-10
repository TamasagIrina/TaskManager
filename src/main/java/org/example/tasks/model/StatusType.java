package org.example.tasks.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="status_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "STATUS_TYPE_ID")
    private String statusTypeId;

    @Column(name="STATUS_NAME", nullable = false)
    private String statusName;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATION_DATE")
    @Builder.Default
    private LocalDateTime creationDate= LocalDateTime.now();
}
