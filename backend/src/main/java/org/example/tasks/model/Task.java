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
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "TASK_NAME", nullable = false, length = 500)
    private String taskName;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "CREATION_DATE", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private LocalDateTime lastUpdateDate;

    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 50)
    private String lastUpdatedBy;

    @Column(name = "CREATED_BY_FULLNAME", length = 300)
    private String createdByFullName;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.creationDate = now;
        this.lastUpdateDate = now;
        if (this.createdBy == null) {
            this.createdBy = "USER";
        }
        if (this.lastUpdatedBy == null) {
            this.lastUpdatedBy = this.createdBy;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateDate = LocalDateTime.now();
        this.lastUpdatedBy = "APP_USER";

    }

    // FK  status_types(status_type_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_TYPE_ID", referencedColumnName = "STATUS_TYPE_ID")
    private StatusType statusType;

    // FK  users(user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private User user;


}
