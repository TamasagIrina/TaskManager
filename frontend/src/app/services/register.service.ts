import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UserDTOCreate } from '../domains/UserDTOCreate';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/register';

  postRegister(user: UserDTOCreate) {
    return this.http.post<String>(this.apiUrl, user,
      {
        responseType: 'text' as 'json',
      }
    );
  }
}
