import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { TaskDTOResponse } from '../interfaces/TaskDTOResponse';
import { TaskCreateDTO } from '../interfaces/TaskDTOCreate';

@Injectable({
  providedIn: 'root'
})
export class ServiceTasksService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/tasks';

  getTasks() {
    return this.http.get<TaskDTOResponse[]>(this.apiUrl);
  }

  getTaskById(taskId: number) {
    const url = `${this.apiUrl}/${taskId}`;
    return this.http.get<TaskDTOResponse>(url);
  }

  addTask(task: TaskCreateDTO) {
    return this.http.post(this.apiUrl, task);
  }

  updateTask(taskId: number, task: TaskCreateDTO) {
    const url = `${this.apiUrl}/update/${taskId}`;
    return this.http.put(url, task);
  }
}
