import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ProjectDTO } from '../domains/ProjectDTOResponse';
import { ProjectCreateDTO } from '../domains/ProjectDTOCreate';
import { UserDTOResponse } from '../domains/UserDTOResponse';

@Injectable({
  providedIn: 'root'
})
export class ServiceProjectService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/projects';

  getProjects(){
    return this.http.get<ProjectDTO[]>(this.apiUrl);
  }

  getProjectById(projectId: number) {
    return this.http.get<ProjectDTO>(`${this.apiUrl}/${projectId}`);
  }

  getProjectsByMember(userId: number) {
    return this.http.get<ProjectDTO[]>(`${this.apiUrl}/user/${userId}`);
  }

  getProjectMembers(projectId: number) {
    return this.http.get<UserDTOResponse[]>(`${this.apiUrl}/${projectId}/members`);
  }

  createProject(project: ProjectCreateDTO) {
    return this.http.post<ProjectDTO>(this.apiUrl, project);
  }

  updateProject(projectId: number, project: ProjectCreateDTO) {
    return this.http.put<ProjectDTO>(`${this.apiUrl}/${projectId}`, project);
  }

  addMembers(projectId: number, memberIds: number[]) {
    return this.http.patch<ProjectDTO>(`${this.apiUrl}/${projectId}/members`, memberIds);
  }

  updateProjectStatus(projectId: number, statusTypeId: string) {
    const params = new HttpParams().set('statusTypeId', statusTypeId);
    return this.http.patch<ProjectDTO>(`${this.apiUrl}/${projectId}/status`, {}, { params });
  }

  deleteProject(projectId: number) {
    return this.http.delete(`${this.apiUrl}/${projectId}`);
  }

}
