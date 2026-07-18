import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, Signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'TasksFrontEndApp';
  // userName: string | null = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user') || '').username : null;
  userName = signal<string>("");

  router = inject(Router);

  ngOnInit() {
    this.setUsername();
  }

  setUsername() {
    this.userName.set(localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user') || '').username : null);
  }

  onLogout(): void {
    localStorage.removeItem("user");
    this.userName.set("");
    this.router.navigate(['/login-register']);
  }
}
