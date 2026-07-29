import { Component, inject, signal } from '@angular/core';
import { TaskCardComponent } from '../../shared/task-card/task-card.component';
import { FormsModule } from '@angular/forms';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
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


  searchSubject = '';
  searchAssignee = '';
  searchStatus = '';
  searchDate = '';

  private serviceTasks = inject(ServiceTasksService);
  private serviceStatuses = inject(ServiceStatusTypeService);

  ngOnInit() {
    this.getStatuses();
    this.onSearch();
  }

  getStatuses() {
    this.serviceStatuses.getStatusTypes().subscribe({
      next: (data) => this.availableStatuses.set(data),
      error: (err) => console.error(err)
    });
  }

  onSearch() {
    this.loading.set(true);
    this.error.set(null);

    const filter: any = {};
    if (this.searchSubject) {
      filter.taskName = this.searchSubject;
    }

    if (this.searchAssignee) {
      filter.username = this.searchAssignee;
    }

    if (this.searchStatus) {
      filter.statusName = this.searchStatus;
    }

    if (this.searchDate) {
      filter.dueDateTime = `${this.searchDate}T00:00:00`;
    }

    if (LocalStorageUtils.getRoleFromToken() === "ADMIN") {
      this.serviceTasks.getFilteredTasks(filter).subscribe({
        next: (data) => {
          this.tasks.set(data);
          this.loading.set(false);
        },
        error: (err) => {
          this.error.set(err.message);
          this.loading.set(false);
        }
      });
    } else {
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

      this.serviceTasks.getFilteredUserTasks(userId, filter).subscribe({
        next: (data) => {
          this.tasks.set(data);
          this.loading.set(false);

        },
        error: (err) => {
          this.error.set(err.message);
          this.loading.set(false);
        }
      });
    }

  }

  clearFilters() {
    this.searchSubject = '';
    this.searchAssignee = '';
    this.searchStatus = '';
    this.searchDate = '';
    this.tasks.set([]);
  }

}
