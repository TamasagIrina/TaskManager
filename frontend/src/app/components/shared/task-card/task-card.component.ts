import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { Router } from '@angular/router';
import { ServiceTasksService } from '../../../services/service-tasks.service';

@Component({
  selector: 'app-task-card',
  imports: [],
  templateUrl: './task-card.component.html',
  styleUrl: './task-card.component.css'
})
export class TaskCardComponent {
  @Input({ required: true }) task!: TaskDTOResponse;

  @Output() deleteTaskEvent = new EventEmitter<any>();

  router = inject(Router);

  serviceTasks = inject(ServiceTasksService);

  openEditTaskModal(task: TaskDTOResponse) {
    this.router.navigate(['/edit-task', task.taskId], { state: { id: task.taskId } });
  }
  onDeleteClick() {
    this.deleteTaskEvent.emit(this.task);
  }
  
}
