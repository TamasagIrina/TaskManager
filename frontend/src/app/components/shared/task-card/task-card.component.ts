import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { Router } from '@angular/router';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import LocalStorageUtils from '../../../utils/localStorageUtils';
import { MatIcon } from '@angular/material/icon';
import { TaskDetailModalComponent } from "../task-detail-modal/task-detail-modal.component";

@Component({
  selector: 'app-task-card',
  imports: [MatIcon, TaskDetailModalComponent],
  templateUrl: './task-card.component.html',
  styleUrl: './task-card.component.css'
})
export class TaskCardComponent {
  @Input({ required: true }) task!: TaskDTOResponse;

  @Output() deleteTaskEvent = new EventEmitter<any>();


  @Output() taskUpdatedEvent = new EventEmitter<TaskDTOResponse>();

  role = signal<string>("");

  router = inject(Router);

  serviceTasks = inject(ServiceTasksService);

  showModal = signal<boolean>(false);

  ngOnInit() {
    this.role.set(LocalStorageUtils.getRoleFromToken()!);
  }
  openDetailModal() {
    this.showModal.set(true);
  }

  closeDetailModal() {
    this.showModal.set(false);
  }

  onTaskUpdated(updated: TaskDTOResponse) {
    this.task = updated;
    this.taskUpdatedEvent.emit(updated);
  }

  openEditTaskModal(task: TaskDTOResponse) {
    this.router.navigate(['/edit-task', task.taskId], { state: { id: task.taskId } });
  }
  onDeleteClick() {
    this.deleteTaskEvent.emit(this.task);
  }

}
