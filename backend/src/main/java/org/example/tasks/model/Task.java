package org.example.tasks.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TASKS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "TASK_NAME", nullable = false, length = 500)
    private String taskName;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    // FK  status_types(status_type_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_TYPE_ID", referencedColumnName = "STATUS_TYPE_ID")
    private StatusType statusType;

    // FK  users(user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private User user;


}
