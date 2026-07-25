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

  router = inject(Router);

  ngOnInit() {
    this.setEmail();
  }

  setEmail() {
    this.email.set(LocalStorageUtils.getEmailFromToken()!);
  }

  onLogout(): void {
    LocalStorageUtils.deleteItem(LocalStorageUtils.tokenKey);
    this.email.set("");
    this.router.navigate(['/login-register']);
  }
}
