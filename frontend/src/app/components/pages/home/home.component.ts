import { Component, computed, inject, signal } from '@angular/core';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { forkJoin } from 'rxjs';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { TaskCardComponent } from "../../shared/task-card/task-card.component";
import { LoadingComponent } from "../../shared/loading/loading.component";
import LocalStorageUtils from '../../../utils/localStorageUtils';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';

@Component({
  selector: 'app-home',
  imports: [TaskCardComponent, LoadingComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  email: string | null = LocalStorageUtils.getEmailFromToken();

  pendingCount = signal<number>(0);
  inProgressCount = signal<number>(0);
  completedCount = signal<number>(0);

  error = signal<string>("");
  loading = signal<boolean>(true);

  overdueTasks = signal<TaskDTOResponse[]>([]);
  upcomingTasks = signal<TaskDTOResponse[]>([]);
  loadingOverdue = signal<boolean>(true);
  loadingUpcoming = signal<boolean>(true);


  totalTasks = computed(() =>
    this.pendingCount() + this.inProgressCount() + this.completedCount()
  );


  pendingPercent = computed(() =>
    this.totalTasks() === 0 ? 0 : (this.pendingCount() / this.totalTasks()) * 100
  );
  inProgressPercent = computed(() =>
    this.totalTasks() === 0 ? 0 : (this.inProgressCount() / this.totalTasks()) * 100
  );
  completedPercent = computed(() =>
    this.totalTasks() === 0 ? 0 : (this.completedCount() / this.totalTasks()) * 100
  );

  private serviceTasks = inject(ServiceTasksService);

  ngOnInit() {
    this.loadDashboardStats();
    this.loadOverdueTasks();
    this.loadUpcomingTasks();

  }
  private getUserId(): number | null {
    const idString = LocalStorageUtils.getIDFromToken();

    if (!idString) {
      this.error.set('User ID not found');
      this.loading.set(false);
      return null;
    }

    const userId = Number(idString);
    if (Number.isNaN(userId)) {
      this.error.set('Invalid user ID');
      this.loading.set(false);
      return null;
    }

    return userId;
  }

  loadDashboardStats() {
    this.loading.set(true);

    const userId = this.getUserId();
    if (userId === null) {
      return;
    }

    forkJoin({
      pending: this.serviceTasks.getTaskCountByUserId(userId, 'Pending'),
      inProgress: this.serviceTasks.getTaskCountByUserId(userId, 'In Progress'),
      completed: this.serviceTasks.getTaskCountByUserId(userId, 'Done')
    }).subscribe({
      next: (results) => {
        this.pendingCount.set(results.pending);
        this.inProgressCount.set(results.inProgress);
        this.completedCount.set(results.completed);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Eroare la încărcarea statisticilor', err);
        this.error.set(err);
        this.loading.set(false);
      }
    });
  }

  loadOverdueTasks() {
    this.loadingOverdue.set(true);

    const userId = this.getUserId();
    if (userId === null) {
      this.loadingOverdue.set(false);
      return;
    }

    this.serviceTasks.getOverdueTasksByUserId(userId).subscribe({
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

  loadUpcomingTasks() {
    this.loadingUpcoming.set(true);

    const userId = this.getUserId();
    if (userId === null) {
      this.loadingUpcoming.set(false);
      return;
    }

    const today = new Date();
    const nextWeek = new Date();
    nextWeek.setDate(today.getDate() + 7);

    // datele ca YYYY-MM-DD pentru Spring Boot LocalDate
    const startStr = today.toISOString().split('T')[0];
    const endStr = nextWeek.toISOString().split('T')[0];

    this.serviceTasks.getTasksDueBetweenForUser(userId, startStr, endStr).subscribe({
      next: (data) => {
        this.upcomingTasks.set(data);
        this.loadingUpcoming.set(false);
      },
      error: (err) => {
        console.error('Eroare la încărcarea taskurilor viitoare', err);
        this.loadingUpcoming.set(false);
      }
    });
  }


}
