package org.example.tasks.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.NumericBooleanConverter;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USERNAME", nullable = false, length = 500)
    private String username;

    @Column(name = "EMAIL", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "BIRTH_DATE", nullable = false)
    private LocalDate birthDate;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "IS_INTERNAL", nullable = false)
    private Boolean isInternal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "users_role_fk"))
    private Role role;


    @PrePersist
    protected void onCreate() {
        this.isInternal = false;
    }
}
