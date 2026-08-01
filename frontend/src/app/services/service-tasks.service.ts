import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { TaskDTOResponse } from '../domains/TaskDTOResponse';
import { TaskCreateDTO } from '../domains/TaskDTOCreate';
import { TaskFilterDTO } from '../domains/TaskFilterDTO';
import { UserTaskStatsDTO } from '../domains/UserTaskStatsDTO';
import { PageResponse } from '../domains/PageResponse';

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

  getFilteredTasks(filter: TaskFilterDTO = {}, page: number = 0, size: number = 8) {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (filter.taskName) {
      params = params.set('taskName', filter.taskName);
    }
    if (filter.statusName) {
      params = params.set('statusName', filter.statusName);
    }
    if (filter.username) {
      params = params.set('username', filter.username);
    }
    if (filter.projectName) {
      params = params.set('projectName', filter.projectName);
    }
    if (filter.dueDateTime) {
      params = params.set('dueDateTime', filter.dueDateTime);
    }

    const url = `${this.apiUrl}/filter`;

    return this.http.get<PageResponse<TaskDTOResponse>>(url, { params });
  }

  getFilteredUserTasks(id: number, filter: TaskFilterDTO = {}, page: number = 0, size: number = 8) {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (filter.taskName) {
      params = params.set('taskName', filter.taskName);
    }
    if (filter.statusName) {
      params = params.set('statusName', filter.statusName);
    }
    if (filter.username) {
      params = params.set('username', filter.username);
    }
    if (filter.projectName) {
      params = params.set('projectName', filter.projectName);
    }
    if (filter.dueDateTime) {
      params = params.set('dueDateTime', filter.dueDateTime);
    }

    const url = `${this.apiUrl}/filter-tasks-user/${id}`;

    return this.http.get<PageResponse<TaskDTOResponse>>(url, { params });
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

  getTaskCountByUserId(id: number, statusName?: string) {
    let params = new HttpParams();
    if (statusName) {
      params = params.set('statusName', statusName);
    }

    return this.http.get<number>(`${this.apiUrl}/user/${id}/count`, { params });
  }

  getOverdueTasksByUserId(id: number) {
    return this.http.get<TaskDTOResponse[]>(`${this.apiUrl}/user/${id}/overdue`);
  }

  getTasksDueBetweenForUser(id: number, start: string, end: string) {
    let params = new HttpParams()
      .set('start', start)
      .set('end', end);
    return this.http.get<TaskDTOResponse[]>(`${this.apiUrl}/user/${id}/due-between`, { params });
  }

  updateTaskStatus(taskId: number, statusTypeId: string) {
    const url = `${this.apiUrl}/${taskId}/status`;
    const params = new HttpParams().set('statusTypeId', statusTypeId);
    return this.http.patch<TaskDTOResponse>(url, {}, { params });
  }

  updateTaskUser(taskId: number, userId: number | null) {
    const url = `${this.apiUrl}/${taskId}/user`;
    let params = new HttpParams();
    if (userId !== null) {
      params = params.set('userId', userId.toString());
    }
    return this.http.patch<TaskDTOResponse>(url, {}, { params });
  }

  updateDueDateTime(taskId: number, dueDate: string) {
    const url = `${this.apiUrl}/${taskId}/due-date-time`;
    const params = new HttpParams().set('dueDate', dueDate);
    return this.http.patch<TaskDTOResponse>(url, {}, { params });
  }
  deleteTask(taskId: number) {
    const url = `${this.apiUrl}/${taskId}`;
    return this.http.delete(url);
  }

  getTaskStatsByUser() {
    return this.http.get<UserTaskStatsDTO[]>(`${this.apiUrl}/stats/by-user`);
  }
}
