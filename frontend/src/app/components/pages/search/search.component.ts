import { Component, computed, inject, signal } from '@angular/core';
import { TaskCardComponent } from '../../shared/task-card/task-card.component';
import { FormsModule } from '@angular/forms';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { TaskFilterDTO } from '../../../domains/TaskFilterDTO';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { LoadingComponent } from "../../shared/loading/loading.component";
import LocalStorageUtils from '../../../utils/localStorageUtils';

@Component({
  selector: 'app-search',
  imports: [TaskCardComponent, FormsModule, LoadingComponent],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent {
  tasks = signal<TaskDTOResponse[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  availableStatuses = signal<StatusTypeDTO[]>([]);

  isAdmin = signal<boolean>(false);

  searchSubject = '';
  searchAssignee = '';
  searchStatus = '';
  searchProject = '';
  searchDate = '';

  currentPage = signal(0);
  totalPages = signal(0);
  pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i));

  private serviceTasks = inject(ServiceTasksService);
  private serviceStatuses = inject(ServiceStatusTypeService);

  ngOnInit() {
    this.isAdmin.set(LocalStorageUtils.getRoleFromToken() === "ADMIN");
    this.getStatuses();
    this.onSearch();
  }

  getStatuses() {
    this.serviceStatuses.getStatusTypes().subscribe({
      next: (data) => this.availableStatuses.set(data),
      error: (err) => console.error(err)
    });
  }

  // apasare pe Search / Clear -> mereu de la prima pagina
  onSearch() {
    this.currentPage.set(0);
    this.loadTasks();
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages() || page === this.currentPage()) {
      return;
    }
    this.currentPage.set(page);
    this.loadTasks();
  }

  private buildFilter(): TaskFilterDTO {
    const filter: TaskFilterDTO = {};
    if (this.searchSubject) {
      filter.taskName = this.searchSubject;
    }
    if (this.searchStatus) {
      filter.statusName = this.searchStatus;
    }
    if (this.searchProject) {
      filter.projectName = this.searchProject;
    }
    if (this.searchDate) {
      filter.dueDateTime = `${this.searchDate}T00:00:00`;
    }
    if (this.isAdmin() && this.searchAssignee) {
      filter.username = this.searchAssignee;
    }
    return filter;
  }

  private loadTasks() {
    this.loading.set(true);
    this.error.set(null);

    const filter = this.buildFilter();
    const page = this.currentPage();

    let request$;
    if (this.isAdmin()) {
      request$ = this.serviceTasks.getFilteredTasks(filter, page);
    } else {
      const idString = LocalStorageUtils.getIDFromToken();
      const userId = idString ? Number(idString) : NaN;
      if (!idString || Number.isNaN(userId)) {
        this.error.set('User ID not found');
        this.loading.set(false);
        return;
      }
      request$ = this.serviceTasks.getFilteredUserTasks(userId, filter, page);
    }

    request$.subscribe({
      next: (data) => {
        if (data.content.length === 0 && data.totalPages > 0 && this.currentPage() > data.totalPages - 1) {
          this.currentPage.set(data.totalPages - 1);
          this.loadTasks();
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

  clearFilters() {
    this.searchSubject = '';
    this.searchAssignee = '';
    this.searchStatus = '';
    this.searchProject = '';
    this.searchDate = '';
    this.onSearch();
  }

}
