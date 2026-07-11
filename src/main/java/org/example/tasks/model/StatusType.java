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
    @Column(name = "STATUS_TYPE_ID", length = 255, nullable = false, updatable = false)
    private String statusTypeId;

    @Column(name = "STATUS_NAME", nullable = false, length = 500)
    private String statusName;

    @Column(name = "CREATION_DATE", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "CREATED_BY", nullable = false, updatable = false, length = 50)
    private String createdBy;

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

        //cand adaugam audentificare cu Spring Security createdBy si createdByFullName se vor lua din token
        //nu mi se pare corect sa le punem noi de mana, le las acum setate default
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateDate = LocalDateTime.now();
        this.lastUpdatedBy = "USER";
    }
}
