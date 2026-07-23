import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { Router } from '@angular/router';
import { TaskCardComponent } from "../../shared/task-card/task-card.component";
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { LoadingComponent } from "../../shared/loading/loading.component";

@Component({
  selector: 'app-my-tasks',
  imports: [TaskCardComponent, LoadingComponent],
  templateUrl: './my-tasks.component.html',
  styleUrl: './my-tasks.component.css'
})
export class MyTasksComponent implements OnInit {
  tasks = signal<TaskDTOResponse[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  availableStatuses = signal<StatusTypeDTO[]>([]);
  activeStatus = '';

  private serviceTasks = inject(ServiceTasksService);
  private serviceStatuses = inject(ServiceStatusTypeService);

  sortedTasks = computed(() => {

    return [...this.tasks()].sort((a, b) => {

      const dateA = new Date(a.dueDate).getTime();
      const dateB = new Date(b.dueDate).getTime();

      return dateA - dateB;
    });
  });

  ngOnInit() {
    this.getTasks();
    this.getStatuses();
  }

  filterByStatus(status: string) {

    if (this.activeStatus === status) {
      return;
    }

    this.activeStatus = status;
    this.getTasks();
  }

  getTasks() {
    this.loading.set(true);
    this.error.set(null);

    const filter = this.activeStatus !== '' ? { statusName: this.activeStatus } : {};

    
    this.serviceTasks.getFilteredTasks(filter).subscribe({
      next: (data) => {
        // setTimeout(() => {
        //   this.tasks.set(data);
        //   this.loading.set(false);
        // }, 200);
        this.tasks.set(data);
        this.loading.set(false);
  
      },
      error: (err) => {
        this.error.set(err.message);
        this.loading.set(false);
      }
    });
  }

  deleteTaskFromList(task: TaskDTOResponse){
    const isConfirmed = window.confirm(`Are you sure you want to delete the task "${task.taskName}"?`);

    if (isConfirmed) {
      this.serviceTasks.deleteTask(task.taskId).subscribe({
        next: () => {
          alert(`Task "${task.taskName}" has been deleted.`);
          
          this.tasks.update(currentTasks => 
            currentTasks.filter(t => t.taskId !== task.taskId)
          );
        },
        error: (err) => { 
          console.error(`Error deleting task "${task.taskName}":`, err);
          alert(`Failed to delete task "${task.taskName}". Please try again later.`);
        } 
      });
    }
    
  }


  getStatuses() {
    this.serviceStatuses.getStatusTypes().subscribe({
      next: (data) => {
        this.availableStatuses.set(data);
      },
      error: (err) => {
        this.error.set(err.message);
      }
    });
  }
  

}
