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
public class StatusType extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "STATUS_TYPE_ID", length = 255, nullable = false, updatable = false)
    private String statusTypeId;

    @Column(name = "STATUS_NAME", nullable = false, length = 500)
    private String statusName;

}
