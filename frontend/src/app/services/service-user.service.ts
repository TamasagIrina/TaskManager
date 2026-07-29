import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UserDTOResponse } from '../domains/UserDTOResponse';
import { UserDTOCreate } from '../domains/UserDTOCreate';
import { AuthRequestDTO } from '../domains/AuthRequestDTO';

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
}
