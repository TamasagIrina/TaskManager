import { Component, inject, signal } from '@angular/core';
import { AuthResponseDTO } from '../../../domains/AuthResponseDTO';
import { ServiceUserService } from '../../../services/service-user.service';
import { UserDTOCreate } from '../../../domains/UserDTOCreate';
import { AuthRequestDTO } from '../../../domains/AuthRequestDTO';
import { Router, RouterLink } from '@angular/router';
import { AppComponent } from '../../../app.component';
import { LoginService } from '../../../services/login.service';
import LocalStorageUtils from '../../../utils/localStorageUtils';
import { RegisterService } from '../../../services/register.service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-login-register',
  imports: [MatIconModule],
  templateUrl: './login-register.component.html',
  styleUrl: './login-register.component.css'
})
export class LoginRegisterComponent {

  errorMessage = signal<string>('');
  isLoading = signal<boolean>(false);

  showPassword = signal<boolean>(false);

  serviceLogin = inject(LoginService);

  serviceRegister = inject(RegisterService);

  appComponent = inject(AppComponent);

  logOrReg: boolean = true;

  router = inject(Router);

  login(email: string, password: string) {
    if (!email || !password) {
      this.errorMessage.set('Please fill in both email and password.');
      return;
    }

    this.errorMessage.set('');
    this.isLoading.set(true);

    const encodedUserDTO: AuthRequestDTO = {
      email: btoa(email),
      password: btoa(password)
    };

    this.serviceLogin.postLogin(encodedUserDTO).subscribe({
      next: (response: any) => {
        this.isLoading.set(false);

        console.log('Login successful:', response);

        if (response.startsWith("403:")) {
          console.error("Incorrect email or password");
          return;
        }

        LocalStorageUtils.setItem(LocalStorageUtils.tokenKey, response);

        this.appComponent.setEmail();

        this.router.navigate(['/home']);
      },
      error: (error) => {
        this.isLoading.set(false);
        this.errorMessage.set(
          error.status === 401 ? 'Incorrect email or password.' : 'Something went wrong. Please try again.'
        );
      }
    });
  }

  register(username: string, email: string, password: string, birthDate: string) {

    if (!username || !email || !password || !birthDate) {
      this.errorMessage.set('Please fill in all fields.');
      return;
    }

    this.errorMessage.set('');
    this.isLoading.set(true);

    const user: UserDTOCreate = {
      username: username,
      email: btoa(email),
      password: btoa(password),
      birthDate: birthDate
    };

    this.serviceRegister.postRegister(user).subscribe({
      next: (response: any) => {
        this.isLoading.set(false);

        LocalStorageUtils.setItem(LocalStorageUtils.tokenKey, response);

        this.appComponent.setEmail();

        this.router.navigate(['/home']);
      },
      error: (error) => {
        this.isLoading.set(false);
        this.errorMessage.set(
          error.status === 409 ? 'Email is already in use.' : 'Registration failed. Please try again.'
        );
      }
    });
  }

  switchToLogin() {
    this.logOrReg = true;
    this.errorMessage.set('');
  }

  switchToRegister() {
    this.logOrReg = false;
    this.errorMessage.set('');
  }

  clearError(): void {
    this.errorMessage.set('');
  }

  togglePasswordVisibility() {
    this.showPassword.update(value => !value);
  }


}
