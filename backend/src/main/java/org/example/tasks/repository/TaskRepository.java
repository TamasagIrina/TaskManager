package org.example.tasks.repository;

import org.example.tasks.dto.response.UserTaskStatsDTO;
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

    long countByUser_UserId(Long userId);

    long countByUser_UserIdAndStatusType_StatusName(Long userId, String statusName);

    List<Task> findByUser_UserIdAndDueDateBefore(Long userId, LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.user.userId = :userId AND t.dueDate BETWEEN :start AND :end")
    List<Task> findTasksDueBetweenForUser(@Param("userId") Long userId,
                                          @Param("start") LocalDate start,
                                          @Param("end") LocalDate end);

    @Query("""
    SELECT new org.example.tasks.dto.response.UserTaskStatsDTO(
        u.userId,
        u.username,
        u.email,
        COUNT(t.taskId),
        SUM(CASE WHEN st.statusName = 'Pending' THEN 1 ELSE 0 END),
        SUM(CASE WHEN st.statusName = 'In Progress' THEN 1 ELSE 0 END),
        SUM(CASE WHEN st.statusName = 'Done' THEN 1 ELSE 0 END),
        SUM(CASE WHEN t.dueDate < CURRENT_DATE AND st.statusName != 'Done' THEN 1 ELSE 0 END)
    )
    FROM User u
    LEFT JOIN Task t ON t.user = u
    LEFT JOIN t.statusType st
    GROUP BY u.userId, u.username, u.email
    ORDER BY COUNT(t.taskId) DESC
    """)
    List<UserTaskStatsDTO> getTaskStatsByUser();


}
