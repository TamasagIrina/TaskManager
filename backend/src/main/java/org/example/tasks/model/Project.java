package org.example.tasks.model;

import jakarta.persistence.*;
import org.example.tasks.model.Auditable;
import org.example.tasks.model.StatusType;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "PROJECT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_ID")
    private Long projectId;

    @Column(name = "PROJECT_NAME", nullable = false, length = 255)
    private String projectName;

    @Column(name = "PROJECT_DESCRIPTION", length = 4000)
    private String projectDescription;

    // FK status_types(status_type_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_TYPE_ID", referencedColumnName = "STATUS_TYPE_ID")
    private StatusType statusType;

  @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
    name = "PROJECT_MEMBERS",
    joinColumns = @JoinColumn(name = "PROJECT_ID"),
    inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
     private List<User> members;

}
