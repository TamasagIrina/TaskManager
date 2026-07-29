import { Component, computed, inject, signal } from '@angular/core';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { forkJoin } from 'rxjs';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { UserTaskStatsDTO } from '../../../domains/UserTaskStatsDTO';
import { TaskCardComponent } from "../../shared/task-card/task-card.component";
import { LoadingComponent } from "../../shared/loading/loading.component";
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  imports: [LoadingComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {

  private serviceTasks = inject(ServiceTasksService);
  private router = inject(Router);

  loadingCounts = signal<boolean>(true);
  loadingOverdue = signal<boolean>(true);
  loadingUserStats = signal<boolean>(true);
  error = signal<string>("");

  pendingCount = signal<number>(0);
  inProgressCount = signal<number>(0);
  completedCount = signal<number>(0);

  overdueTasks = signal<TaskDTOResponse[]>([]);
  userStats = signal<UserTaskStatsDTO[]>([]);

  noTasksExpanded = signal<boolean>(false);

  totalTasks = computed(() =>
    this.pendingCount() + this.inProgressCount() + this.completedCount()
  );

  activeUsersCount = computed(() =>
    this.userStats().filter(u => u.totalTasks > 0).length
  );

  overdueCountTotal = computed(() => this.overdueTasks().length);

  criticalOverdueCount = computed(() => {
    const fiveDaysAgo = new Date();
    fiveDaysAgo.setDate(fiveDaysAgo.getDate() - 5);
    return this.overdueTasks().filter(t => new Date(t.dueDate) < fiveDaysAgo).length;
  });

  usersWithoutTasksList = computed(() =>
    this.userStats().filter(u => u.totalTasks === 0)
  );

  usersWithoutTasks = computed(() => this.usersWithoutTasksList().length);

  userBars = computed(() =>
    this.userStats()
      .slice()
      .sort((a, b) => b.totalTasks - a.totalTasks)
      .map(u => ({
        ...u,
        pendingPct: u.totalTasks === 0 ? 0 : (u.pendingCount / u.totalTasks) * 100,
        inProgressPct: u.totalTasks === 0 ? 0 : (u.inProgressCount / u.totalTasks) * 100,
        completedPct: u.totalTasks === 0 ? 0 : (u.completedCount / u.totalTasks) * 100,
      }))
  );

  ngOnInit() {
    this.loadCounts();
    this.loadOverdue();
    this.loadUserStats();
  }

  loadCounts() {
    this.loadingCounts.set(true);
    forkJoin({
      pending: this.serviceTasks.getTaskCount('Pending'),
      inProgress: this.serviceTasks.getTaskCount('In Progress'),
      completed: this.serviceTasks.getTaskCount('Done')
    }).subscribe({
      next: (results) => {
        this.pendingCount.set(results.pending);
        this.inProgressCount.set(results.inProgress);
        this.completedCount.set(results.completed);
        this.loadingCounts.set(false);
      },
      error: (err) => {
        console.error('Eroare la încărcarea numărătorilor', err);
        this.loadingCounts.set(false);
      }
    });
  }

  loadOverdue() {
    this.loadingOverdue.set(true);
    this.serviceTasks.getOverdueTasks().subscribe({
      next: (data) => {
        this.overdueTasks.set(data);
        this.loadingOverdue.set(false);
      },
      error: (err) => {
        console.error('Eroare la încărcarea taskurilor restante', err);
        this.loadingOverdue.set(false);
      }
    });
  }

  loadUserStats() {
    this.loadingUserStats.set(true);
    this.serviceTasks.getTaskStatsByUser().subscribe({
      next: (data) => {
        this.userStats.set(data);
        this.loadingUserStats.set(false);
      },
      error: (err) => {
        console.error('Eroare la încărcarea statisticilor per utilizator', err);
        this.loadingUserStats.set(false);
      }
    });
  }

  toggleNoTasksList() {
    this.noTasksExpanded.update(v => !v);
  }

  addTaskForUser(userId: number) {
    this.router.navigate(['/new-task'], { state: { userId } });
  }

}
