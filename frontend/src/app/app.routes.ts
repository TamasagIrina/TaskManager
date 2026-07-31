import { Routes } from '@angular/router';
import { HomeComponent } from './components/pages/home/home.component';
import { MyTasksComponent } from './components/pages/my-tasks/my-tasks.component';
import { SearchComponent } from './components/pages/search/search.component';
import { LoginRegisterComponent } from './components/pages/login-register/login-register.component';
import { logginGuardServiceGuard } from './services/loggin-guard-service.guard';
import { AddEditTaskComponent } from './components/shared/add-edit-task/add-edit-task.component';
import { AddEditProjectComponent } from './components/shared/add-edit-project/add-edit-project.component';
import { AllProjectsComponent } from './components/pages/all-projects/all-projects.component';
import { MyProjectsComponent } from './components/pages/my-projects/my-projects.component';
import { guestOnlyGuard } from './services/guest-only.guard';
import { adminGuard } from './services/admin.guard';
import { AdminDashboardComponent } from './components/pages/admin-dashboard/admin-dashboard.component';

export const routes: Routes = [
    { path: 'home', component: HomeComponent, canActivate: [logginGuardServiceGuard] },
    { path: 'my-tasks', component: MyTasksComponent, canActivate: [logginGuardServiceGuard] },
    { path: 'my-projects', component: MyProjectsComponent, canActivate: [logginGuardServiceGuard] },
    { path: 'search', component: SearchComponent, canActivate: [logginGuardServiceGuard] },
    { path: 'admin-dashboard', component: AdminDashboardComponent, canActivate: [adminGuard] },
    { path: 'new-task', component: AddEditTaskComponent, canActivate: [logginGuardServiceGuard] },
    { path: 'edit-task/:id', component: AddEditTaskComponent, canActivate: [adminGuard] },
    { path: 'new-project', component: AddEditProjectComponent, canActivate: [adminGuard] },
    { path: 'edit-project/:id', component: AddEditProjectComponent, canActivate: [adminGuard] },
    { path: 'all-projects', component: AllProjectsComponent, canActivate: [adminGuard] },
    { path: 'login-register', component: LoginRegisterComponent, canActivate: [guestOnlyGuard] },

    { path: '**', redirectTo: 'home' }

];
