import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import LocalStorageUtils from '../utils/localStorageUtils';

export const guestOnlyGuard: CanActivateFn = (route, state) => {
   const router = inject(Router);
   const token= LocalStorageUtils.getItem(LocalStorageUtils.tokenKey);
   if(token){
    return router.navigate(['home']).then(() => false);
   }
   return true;

};
