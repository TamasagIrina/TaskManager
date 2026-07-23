import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { TaskDTOResponse } from '../domains/TaskDTOResponse';
import { TaskCreateDTO } from '../domains/TaskDTOCreate';
import { TaskFilterDTO } from '../domains/TaskFilterDTO';

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

  getFilteredTasks(filter: TaskFilterDTO = {}) {
    let params = new HttpParams();

    if (filter.taskName) {
      params = params.set('taskName', filter.taskName);
    }
    if (filter.statusName) {
      params = params.set('statusName', filter.statusName);
    }
    if (filter.username) {
      params = params.set('username', filter.username);
    }
    if (filter.dueDateTime) {
      params = params.set('dueDateTime', filter.dueDateTime);
    }

    const url = `${this.apiUrl}/filter`;

    return this.http.get<TaskDTOResponse[]>(url, { params });
  }

  getTaskCount(statusName?: string) {
    let params = new HttpParams();
    if (statusName) {
      params = params.set('statusName', statusName);
    }

    return this.http.get<number>(`${this.apiUrl}/count`, { params });
  }

  getOverdueTasks() {
    return this.http.get<TaskDTOResponse[]>(`${this.apiUrl}/overdue`);
  }

  getTasksDueBetween(start: string, end: string) {
    let params = new HttpParams()
      .set('start', start)
      .set('end', end);
    return this.http.get<TaskDTOResponse[]>(`${this.apiUrl}/due-between`, { params });
  }

  addTask(task: TaskCreateDTO) {
    return this.http.post(this.apiUrl, task);

  }

  updateTask(taskId: number, task: TaskCreateDTO) {
    const url = `${this.apiUrl}/update/${taskId}`;
    return this.http.put(url, task);
  }

  updateTaskStatus(taskId: number, statusName: string) {
    const url = `${this.apiUrl}${taskId}/status`;

    let params = new HttpParams();
    if (statusName) {
      params = params.set('statusName', statusName);
    }
    return this.http.patch(url, { params });

  }

  updateTaskUser(taskId: number, userName: string) {
    const url = `${this.apiUrl}${taskId}/user`;

    let params = new HttpParams();
    if (userName) {
      params = params.set('statusName', userName);
    }
    return this.http.patch(url, { params });
  }

  updateDueDateTime(taskId: number, dueDateTime: string) {
    const url = `${this.apiUrl}${taskId}/due-date-time`;

    let params = new HttpParams();
    if (dueDateTime) {
      params = params.set('statusName', dueDateTime);
    }
    return this.http.patch(url, { params });

  }

  deleteTask(taskId: number) {
    const url = `${this.apiUrl}/${taskId}`;
    return this.http.delete(url);
  }
}
