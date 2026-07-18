package org.example.tasks.repository;

import org.example.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDueDateBefore(LocalDate date);

    long countByStatusType_StatusTypeId(String statusTypeId);

    long countByStatusType_StatusName(String statusName);

    List<Task> findByUser_UserId(Long oldUserId);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksDueBetween(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);


}
