import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UserDTOResponse } from '../interfaces/UserDTOResponse';
import { UserDTOCreate } from '../interfaces/UserDTOCreate';
import { AuthRequestDTO } from '../interfaces/AuthRequestDTO';

@Injectable({
  providedIn: 'root'
})
export class ServiceUserService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/users';

  getUsers() {
    return this.http.get<UserDTOResponse[]>(this.apiUrl);
  }

  login(authRequest: AuthRequestDTO) {
    const url = `${this.apiUrl}/login`;
    return this.http.post(url, authRequest);
  }

  register(user: UserDTOCreate) {
    const url = `${this.apiUrl}/register`;
    return this.http.post(url, user);
  }
}
