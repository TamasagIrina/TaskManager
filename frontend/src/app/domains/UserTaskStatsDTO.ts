export interface UserTaskStatsDTO {
  userId: number;
  username: string;
  email: string;
  totalTasks: number;
  pendingCount: number;
  inProgressCount: number;
  completedCount: number;
  overdueCount: number;
}