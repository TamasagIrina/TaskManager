import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UserDTOResponse } from '../domains/UserDTOResponse';
import { UserDTOCreate } from '../domains/UserDTOCreate';
import { AuthRequestDTO } from '../domains/AuthRequestDTO';
import { RoleDTO } from '../domains/RoleDTO';

@Injectable({
  providedIn: 'root'
})
export class ServiceUserService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/users';

  getUsers() {
    return this.http.get<UserDTOResponse[]>(this.apiUrl);
  }

  deleteUser(userId: number) {
    const url = `${this.apiUrl}/${userId}`;
    return this.http.delete<void>(url);
  }

  reassignAndDeleteUser(oldUserId: number, newUserId: number) {
    const url = `${this.apiUrl}/${oldUserId}/reassign-to/${newUserId}`;
    return this.http.delete<void>(url);
  }

  getRoles() {
    return this.http.get<RoleDTO[]>(`${this.apiUrl}/roles`);
  }

  updateUserRole(userId: number, roleId: number) {
    const params = new HttpParams().set('roleId', roleId);
    return this.http.patch<UserDTOResponse>(`${this.apiUrl}/${userId}/role`, {}, { params });
  }
}
