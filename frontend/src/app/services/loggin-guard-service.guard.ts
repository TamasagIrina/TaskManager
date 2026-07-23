import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import LocalStorageUtils from '../utils/localStorageUtils';

export const logginGuardServiceGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = LocalStorageUtils.getItem(LocalStorageUtils.tokenKey);
  if (token) {
    return true;
  } else {
    console.log('User is not logged in. Redirecting to login page.');
    router.navigate(['/login-register']);
    return false;
  }
};
