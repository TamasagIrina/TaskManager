package org.example.tasks.repository;

import org.example.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDueDateBefore(LocalDate date);
    long countByStatusType_StatusTypeId(String statusTypeId);

}
