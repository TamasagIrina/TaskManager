import { Injectable, signal } from '@angular/core';
import LocalStorageUtils from '../utils/localStorageUtils';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  email = signal<string>("");
  role = signal<string>("");

  loadFromStorage() {
    this.email.set(LocalStorageUtils.getItem("user_email") ?? "");
    this.role.set(LocalStorageUtils.getRoleFromToken() ?? "");
  }

  clear() {
    LocalStorageUtils.deleteItem(LocalStorageUtils.tokenKey);
    LocalStorageUtils.deleteItem("user_email");
    this.email.set("");
    this.role.set("");
  }
}
