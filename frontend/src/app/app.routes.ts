import { Routes } from '@angular/router';
import { HomeComponent } from './components/pages/home/home.component';
import { MyTasksComponent } from './components/pages/my-tasks/my-tasks.component';
import { SearchComponent } from './components/pages/search/search.component';
import { LoginRegisterComponent } from './components/pages/login-register/login-register.component';
import { logginGuardServiceGuard } from './services/loggin-guard-service.guard';
import { AddEditTaskComponent } from './components/shared/add-edit-task/add-edit-task.component';

export const routes: Routes = [
    {path: 'home',component: HomeComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'my-tasks',component: MyTasksComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'search',component: SearchComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'new-task',component: AddEditTaskComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'edit-task/:id',component: AddEditTaskComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'login-register',component: LoginRegisterComponent},
    
    { path: '**', redirectTo: 'home' }

];
