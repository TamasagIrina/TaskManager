import { Routes } from '@angular/router';
import { HomeComponent } from './components/pages/home/home.component';
import { MyTasksComponent } from './components/pages/my-tasks/my-tasks.component';
import { SearchComponent } from './components/pages/search/search.component';
import { NewTaskComponent } from './components/pages/new-task/new-task.component';
import { LoginRegisterComponent } from './components/pages/login-register/login-register.component';
import { EditTaskComponent } from './components/shared/edit-task/edit-task.component';
import { logginGuardServiceGuard } from './services/loggin-guard-service.guard';

export const routes: Routes = [
    {path: 'home',component: HomeComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'my-tasks',component: MyTasksComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'search',component: SearchComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'new-task',component: NewTaskComponent, canActivate: [logginGuardServiceGuard]},
    {path: 'login-register',component: LoginRegisterComponent},
    {path: 'edit-task/:id',component: EditTaskComponent, canActivate: [logginGuardServiceGuard]},

];
