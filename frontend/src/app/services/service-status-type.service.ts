import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { StatusTypeDTO } from '../domains/StatusTypeDTO';

@Injectable({
  providedIn: 'root'
})
export class ServiceStatusTypeService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/statuses';

  getStatusTypes() {
    return this.http.get<StatusTypeDTO[]>(this.apiUrl);
  }
}
