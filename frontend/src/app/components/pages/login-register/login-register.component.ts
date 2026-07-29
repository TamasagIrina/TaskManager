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
import { AuthStateService } from '../../../services/auth-state.service';

@Component({
  selector: 'app-login-register',
  imports: [MatIconModule],
  templateUrl: './login-register.component.html',
  styleUrl: './login-register.component.css'
})
export class LoginRegisterComponent {

  errorMessage = signal<string>('');
  isLoading = signal<boolean>(false);

  successMessage = signal<string>('');

  showPassword = signal<boolean>(false);

  serviceLogin = inject(LoginService);

  serviceRegister = inject(RegisterService);

  authState = inject(AuthStateService);

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

        LocalStorageUtils.setItem("user_email", LocalStorageUtils.getEmailFromToken());

        this.authState.loadFromStorage();
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

  register(username: string, email: string, password: string, confirmPassword: string, birthDate: string) {
    this.successMessage.set("");

    if (!username || !email || !password || !confirmPassword || !birthDate) {
      this.errorMessage.set('Please fill in all fields.');
      return;
    }

    if (password !== confirmPassword) {
      this.errorMessage.set('Passwords do not match.');
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
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Account created! Please sign in.');

        setTimeout(() => {
          this.switchToLogin();
        }, 200);


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
