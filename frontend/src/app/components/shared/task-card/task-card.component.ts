import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { Router } from '@angular/router';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import LocalStorageUtils from '../../../utils/localStorageUtils';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-task-card',
  imports: [MatIcon],
  templateUrl: './task-card.component.html',
  styleUrl: './task-card.component.css'
})
export class TaskCardComponent {
  @Input({ required: true }) task!: TaskDTOResponse;

  @Output() deleteTaskEvent = new EventEmitter<any>();

  role= signal<string>("");

  router = inject(Router);

  serviceTasks = inject(ServiceTasksService);

  ngOnInit(){
    this.role.set(LocalStorageUtils.getRoleFromToken()!);
  }

  openEditTaskModal(task: TaskDTOResponse) {
    this.router.navigate(['/edit-task', task.taskId], { state: { id: task.taskId } });
  }
  onDeleteClick() {
    this.deleteTaskEvent.emit(this.task);
  }
  
}
