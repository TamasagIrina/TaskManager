import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { ServiceUserService } from '../../../services/service-user.service';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { UserDTOResponse } from '../../../domains/UserDTOResponse';
import LocalStorageUtils from '../../../utils/localStorageUtils';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { ServiceProjectService } from '../../../services/service-project.service';

@Component({
  selector: 'app-task-detail-modal',
  imports: [FormsModule, MatIcon],
  templateUrl: './task-detail-modal.component.html',
  styleUrl: './task-detail-modal.component.css'
})
export class TaskDetailModalComponent {

  @Input({ required: true }) task!: TaskDTOResponse;
  @Output() closeModal = new EventEmitter<void>();
  @Output() taskUpdated = new EventEmitter<TaskDTOResponse>();

  private serviceTasks = inject(ServiceTasksService);
  private serviceProject = inject(ServiceProjectService);
  private serviceStatusType = inject(ServiceStatusTypeService);

  isAdmin = signal<boolean>(false);

  statusTypes = signal<StatusTypeDTO[]>([]);
  users = signal<UserDTOResponse[]>([]);

  editingStatus = signal(false);
  editingDueDate = signal(false);
  editingUser = signal(false);

  selectedStatusId = signal<string>('');
  selectedDueDate = signal<string>('');
  selectedUserId = signal<number | null>(null);

  saving = signal(false);
  error = signal<string>('');

  ngOnInit() {
    this.isAdmin.set(LocalStorageUtils.getRoleFromToken() === 'ADMIN');
    this.selectedStatusId.set(this.task.statusTypeId);
    this.selectedDueDate.set(this.task.dueDate);
    this.selectedUserId.set(this.task.userId);

    this.loadStatusTypes();
    if (this.isAdmin()) {
      this.loadUsers();
    }
  }

  loadStatusTypes() {
    this.serviceStatusType.getStatusTypes().subscribe({
      next: (data) => this.statusTypes.set(data),
      error: (err) => console.error('Error loading status types', err)
    });
  }

  loadUsers() {
    this.serviceProject.getProjectMembers(this.task.projectId).subscribe({
      next: (data) => this.users.set(data),
      error: (err) => console.error('Error loading users', err)
    });
  }

  toggleEditStatus() {
    this.error.set('');
    this.editingStatus.set(!this.editingStatus());
  }

  toggleEditDueDate() {
    this.error.set('');
    this.editingDueDate.set(!this.editingDueDate());
  }

  toggleEditUser() {
    this.error.set('');
    this.editingUser.set(!this.editingUser());
  }

  saveStatus() {
    this.saving.set(true);
    this.serviceTasks.updateTaskStatus(this.task.taskId, this.selectedStatusId()).subscribe({
      next: (updated) => {
        this.task = updated;
        this.editingStatus.set(false);
        this.saving.set(false);
        this.taskUpdated.emit(updated);
      },
      error: () => {
        this.error.set('Could not update status.');
        this.saving.set(false);
      }
    });
  }

  saveDueDate() {
    this.error.set("");
    if (!this.isDueDateInFuture(this.selectedDueDate())) {
      this.error.set('Due date must be in the future.');
      return;
    }

    this.saving.set(true);

    this.serviceTasks.updateDueDateTime(this.task.taskId, this.selectedDueDate()).subscribe({
      next: (updated) => {
        this.task = updated;
        this.editingDueDate.set(false);
        this.saving.set(false);
        this.taskUpdated.emit(updated);
      },
      error: () => {
        this.error.set('Could not update due date.');
        this.saving.set(false);
      }
    });
  }

  private isDueDateInFuture(value: string): boolean {
    if (!value) return false;
    const inputDate = new Date(value);
    const now = new Date();
    return inputDate.getTime() > now.getTime();
  }

  saveUser() {
    this.saving.set(true);
    this.serviceTasks.updateTaskUser(this.task.taskId, this.selectedUserId()).subscribe({
      next: (updated) => {
        this.task = updated;
        this.editingUser.set(false);
        this.saving.set(false);
        this.taskUpdated.emit(updated);
      },
      error: () => {
        this.error.set('Could not update assignee.');
        this.saving.set(false);
      }
    });
  }

  onClose() {
    this.closeModal.emit();
  }

}
