import { CanActivateFn, Router } from '@angular/router';
import { LoadingComponent } from '../components/shared/loading/loading.component';
import LocalStorageUtils from '../utils/localStorageUtils';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = (route, state) => {
  const role= LocalStorageUtils.getRoleFromToken();
  const token= LocalStorageUtils.getItem(LocalStorageUtils.tokenKey);
  const router = inject(Router);
  if(role==="ADMIN" && token!==null)
  {
    return true;
  }else{
    router.navigate(["/home"]);
  }
  return false;
};
