import { Component, computed, inject, signal } from '@angular/core';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { forkJoin } from 'rxjs';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { TaskCardComponent } from "../../shared/task-card/task-card.component";
import { LoadingComponent } from "../../shared/loading/loading.component";

@Component({
  selector: 'app-home',
  imports: [TaskCardComponent, LoadingComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  userName: string | null = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user') || '').username : null;
  pendingCount = signal<number>(0);
  inProgressCount = signal<number>(0);
  completedCount = signal<number>(0);

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

  loadDashboardStats() {
    this.loading.set(true);


    forkJoin({
      pending: this.serviceTasks.getTaskCount('Pending'),
      inProgress: this.serviceTasks.getTaskCount('In Progress'),
      completed: this.serviceTasks.getTaskCount('Done')
    }).subscribe({
      next: (results) => {
        this.pendingCount.set(results.pending);
        this.inProgressCount.set(results.inProgress);
        this.completedCount.set(results.completed);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Eroare la încărcarea statisticilor', err);
        this.loading.set(false);
      }
    });
  }

  loadOverdueTasks() {
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

  loadUpcomingTasks() {
    this.loadingUpcoming.set(true);
    const today = new Date();
    const nextWeek = new Date();
    nextWeek.setDate(today.getDate() + 7);

    // datele ca YYYY-MM-DD pentru Spring Boot LocalDate
    const startStr = today.toISOString().split('T')[0];
    const endStr = nextWeek.toISOString().split('T')[0];

    this.serviceTasks.getTasksDueBetween(startStr, endStr).subscribe({
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
