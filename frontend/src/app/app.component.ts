import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, Signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import LocalStorageUtils from './utils/localStorageUtils';
import { AuthStateService } from './services/auth-state.service';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'TasksFrontEndApp';

  authState = inject(AuthStateService);
  router = inject(Router);

  ngOnInit() {
    this.authState.loadFromStorage();
  }

  onLogout(): void {
    this.authState.clear();
    this.router.navigate(['/login-register']);
  }
}
