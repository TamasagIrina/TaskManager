import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { Router } from '@angular/router';
import { TaskCardComponent } from "../../shared/task-card/task-card.component";
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { LoadingComponent } from "../../shared/loading/loading.component";
import LocalStorageUtils from '../../../utils/localStorageUtils';
import { ProjectDTO } from '../../../domains/ProjectDTOResponse';
import { ServiceProjectService } from '../../../services/service-project.service';
import { TaskFilterDTO } from '../../../domains/TaskFilterDTO';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-my-tasks',
  imports: [TaskCardComponent, LoadingComponent, FormsModule],
  templateUrl: './my-tasks.component.html',
  styleUrl: './my-tasks.component.css'
})
export class MyTasksComponent implements OnInit {
  tasks = signal<TaskDTOResponse[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  availableStatuses = signal<StatusTypeDTO[]>([]);
  activeStatus = '';

  myProjects = signal<ProjectDTO[]>([]);
  activeProject = '';

  currentPage = signal(0);
  totalPages = signal(0);
  pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i));

  private serviceTasks = inject(ServiceTasksService);
  private serviceStatuses = inject(ServiceStatusTypeService);
  private serviceProjects = inject(ServiceProjectService);

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
    this.getMyProjects();
  }

  filterByStatus(status: string) {

    if (this.activeStatus === status) {
      return;
    }

    this.activeStatus = status;
    this.currentPage.set(0);
    this.getTasks();
  }

  filterByProject() {
    this.currentPage.set(0);
    this.getTasks();
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages() || page === this.currentPage()) {
      return;
    }
    this.currentPage.set(page);
    this.getTasks();
  }

  getTasks() {
    this.loading.set(true);
    this.error.set(null);

    const filter: TaskFilterDTO = {};
    if (this.activeStatus !== '') {
      filter.statusName = this.activeStatus;
    }
    if (this.activeProject !== '') {
      filter.projectName = this.activeProject;
    }

    const idString = LocalStorageUtils.getIDFromToken();

    if (!idString) {
      this.error.set('User ID not found');
      this.loading.set(false);
      return;
    }

    const userId = Number(idString);
    if (Number.isNaN(userId)) {
      this.error.set('Invalid user ID');
      this.loading.set(false);
      return;
    }

    this.serviceTasks.getFilteredUserTasks(userId, filter, this.currentPage()).subscribe({
      next: (data) => {
        // daca pagina curenta a ramas goala (ex. dupa delete), sari pe ultima pagina valida
        if (data.content.length === 0 && data.totalPages > 0 && this.currentPage() > data.totalPages - 1) {
          this.currentPage.set(data.totalPages - 1);
          this.getTasks();
          return;
        }

        this.tasks.set(data.content);
        this.totalPages.set(data.totalPages);
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
          this.getTasks();
        },
        error: (err) => { 
          console.error(`Error deleting task "${task.taskName}":`, err);
          alert(`Failed to delete task "${task.taskName}". Please try again later.`);
        } 
      });
    }
    
  }

  updateTaskFromList(task: TaskDTOResponse){
    this.getTasks();
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

  getMyProjects() {
    const idString = LocalStorageUtils.getIDFromToken();
    if (!idString) {
      return;
    }

    const userId = Number(idString);
    if (Number.isNaN(userId)) {
      return;
    }

    this.serviceProjects.getProjectsByMember(userId).subscribe({
      next: (data) => {
        this.myProjects.set(data);
      },
      error: (err) => {
        console.error('Eroare la încărcarea proiectelor:', err);
      }
    });
  }
  

}
