import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { TaskDTOResponse } from '../../../interfaces/TaskDTOResponse';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-tasks',
  imports: [],
  templateUrl: './my-tasks.component.html',
  styleUrl: './my-tasks.component.css'
})
export class MyTasksComponent implements OnInit {
  tasks = signal<TaskDTOResponse[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  router = inject(Router);

  private service = inject(ServiceTasksService);

  sortedTasks = computed(() => {
  
    return [...this.tasks()].sort((a, b) => {

      const dateA = new Date(a.dueDate).getTime();
      const dateB = new Date(b.dueDate).getTime();
      
      return dateA - dateB; 
    });
  });

  ngOnInit() {
    this.loading.set(true);

    this.service.getTasks().subscribe({
      next: (data) => {
        this.tasks.set(data);
        this.loading.set(false);

        console.log('Tasks:', this.tasks());
      },
      error: (err) => {
        this.error.set(err.message);
        this.loading.set(false);
      }
    });
  }
  openEditTaskModal(task: TaskDTOResponse) {
    this.router.navigate(['/edit-task', task.taskId], { state: { id: task.taskId } });
  }

}
