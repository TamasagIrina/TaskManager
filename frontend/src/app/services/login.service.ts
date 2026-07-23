import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { AuthRequestDTO } from "../domains/AuthRequestDTO";

@Injectable({
    providedIn: 'root'
})
export class LoginService {

    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:8080/login';

    postLogin(authRequst: AuthRequestDTO) {
        return this.http.post<String>(this.apiUrl, authRequst,
            {
                responseType: 'text' as 'json',
            }
        );
    }

}