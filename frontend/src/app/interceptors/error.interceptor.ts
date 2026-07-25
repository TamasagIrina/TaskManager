import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import LocalStorageUtils from '../utils/localStorageUtils';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      if ((error.status === 401 || error.status === 403 ||  error.status === 409) && !req.url.includes('login') && !req.url.includes('register')) {
        LocalStorageUtils.deleteItem(LocalStorageUtils.tokenKey);
        router.navigate(['/login-register']);
      }

      return throwError(() => error);
    })
  );
};
