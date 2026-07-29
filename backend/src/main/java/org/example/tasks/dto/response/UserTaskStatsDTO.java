package org.example.tasks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskStatsDTO {
    private Long userId;
    private String username;
    private String email;
    private long totalTasks;
    private long pendingCount;
    private long inProgressCount;
    private long completedCount;
    private long overdueCount;
}
