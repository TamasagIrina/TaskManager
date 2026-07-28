package org.example.tasks.model;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public  abstract class Auditable {

    @CreatedDate                                      // populat automat cu data/ora curentă, o singură dată, la creare
    @Column(name = "CREATION_DATE", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @CreatedBy                                        // populat automat cu valoarea din AuditorAware, o singură dată, la creare
    @Column(name = "CREATED_BY", nullable = false, updatable = false, length = 50)
    private String createdBy;

    @LastModifiedDate                                 // populat automat la fiecare update (și la creare)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    private LocalDateTime lastUpdateDate;

    @LastModifiedBy                                   // populat automat la fiecare update (și la creare)
    @Column(name = "LAST_UPDATED_BY", nullable = false, length = 50)
    private String lastUpdatedBy;

    @Column(name = "CREATED_BY_FULLNAME", length = 300)  // il setam din service
    private String createdByFullName;
}
