import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const logginGuardServiceGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  if (localStorage.getItem('user')) {
    return true;
  } else {
    console.log('User is not logged in. Redirecting to login page.');
    router.navigate(['/login-register']);
    return false;
  }
};
