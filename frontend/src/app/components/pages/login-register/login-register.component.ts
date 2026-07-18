import { Component, inject, signal } from '@angular/core';
import { AuthResponseDTO } from '../../../domains/AuthResponseDTO';
import { ServiceUserService } from '../../../services/service-user.service';
import { UserDTOCreate } from '../../../domains/UserDTOCreate';
import { AuthRequestDTO } from '../../../domains/AuthRequestDTO';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login-register',
  imports: [],
  templateUrl: './login-register.component.html',
  styleUrl: './login-register.component.css'
})
export class LoginRegisterComponent {
  authResponse = signal<AuthResponseDTO | null>(null);

  serviceUser = inject(ServiceUserService);

  

  logOrReg: boolean = true;

  router= inject(Router);

  login(email: string, password: string) {
    const authRequest: AuthRequestDTO = { email, password };
    this.serviceUser.login(authRequest).subscribe({
      next: (response: any) => {
        this.authResponse.set(response);
        console.log('Login successful:', response);

        localStorage.setItem('user', JSON.stringify(response.user));

    
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Login failed:', error);
      }
    }); 
  }

  register(username: string, email: string, password: string, birthDate: string) {
  
    const user: UserDTOCreate = { username, email, password, birthDate };

    this.serviceUser.register(user).subscribe({
      next: (response: any) => {
        this.authResponse.set(response);
        console.log('Registration successful:', response);

        this.switchToLogin();
      },
      error: (error) => {
        console.error('Registration failed:', error);
      }
    });
  }

  switchToLogin() {
    this.logOrReg = true;
  }

  switchToRegister() {
    this.logOrReg = false;
  }
    



}
