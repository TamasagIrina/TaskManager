import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, Signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import LocalStorageUtils from './utils/localStorageUtils';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'TasksFrontEndApp';
  email = signal<string>("");

  role= signal<string>("");

  router = inject(Router);

  ngOnInit() {
    this.setEmail();
  }

  setEmail() {
    this.email.set(LocalStorageUtils.getItem("user_email")!);
    this.role.set(LocalStorageUtils.getRoleFromToken()!);
  }

  onLogout(): void {
    LocalStorageUtils.deleteItem(LocalStorageUtils.tokenKey);
    this.email.set("");
    this.router.navigate(['/login-register']);
  }
}
