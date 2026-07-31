package org.example.tasks.repository;

import org.example.tasks.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public  interface  ProjectRepository   extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.userId = :userId")
    List<Project> findByMemberId(@Param("userId") Long userId);
}
