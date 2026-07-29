import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import LocalStorageUtils from '../utils/localStorageUtils';
import { AuthStateService } from '../services/auth-state.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authState = inject(AuthStateService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      if ((error.status === 401 || error.status === 403 || error.status === 409) && !req.url.includes('login') && !req.url.includes('register')) {
        authState.clear();
        router.navigate(['/login-register']);
      }

      return throwError(() => error);
    })
  );
};
