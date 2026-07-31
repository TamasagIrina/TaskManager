import { Component, inject, Input, input, output, signal } from '@angular/core';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { UserDTOResponse } from '../../../domains/UserDTOResponse';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { TaskCreateDTO } from '../../../domains/TaskDTOCreate';
import { LoadingComponent } from "../loading/loading.component";
import { Router } from '@angular/router';
import { ProjectDTO } from '../../../domains/ProjectDTOResponse';
import { ServiceProjectService } from '../../../services/service-project.service';
import LocalStorageUtils from '../../../utils/localStorageUtils';

@Component({
  selector: 'app-add-edit-task',
  imports: [ReactiveFormsModule, LoadingComponent],
  templateUrl: './add-edit-task.component.html',
  styleUrl: './add-edit-task.component.css'
})
export class AddEditTaskComponent {

  taskToEdit = signal<TaskDTOResponse | null>(null);

  router = inject(Router);

  id = this.router.getCurrentNavigation()?.extras.state?.['id'];

  preselectedUserId = this.router.getCurrentNavigation()?.extras.state?.['userId'];


  private fb = inject(FormBuilder);
  private serviceTask = inject(ServiceTasksService);
  private serviceStatusType = inject(ServiceStatusTypeService);
  private serviceProject = inject(ServiceProjectService);

  users = signal<UserDTOResponse[]>([]);
  statusTypes = signal<StatusTypeDTO[]>([]);
  projects = signal<ProjectDTO[]>([]);

  isAdmin = signal<boolean>(false);
  isSaving = signal(false);
  private currentUserId: number | null = null;

  isLoadingUsers = signal(false);
  errorUsers = signal<string | null>(null);

  isLoadingProjects = signal(false);
  errorProjects = signal<string | null>(null);

  isLoadingStatusTypes = signal(false);
  errorStatusTypes = signal<string | null>(null);

  taskForm = this.fb.nonNullable.group({
    taskName: ['', [Validators.required, Validators.maxLength(500)]],
    statusTypeId: ['', [Validators.required, Validators.maxLength(255)]],
    projectId: [0,[Validators.required, Validators.min(1)]],
    userId: [0, [Validators.required, Validators.min(1)]],
    dueDate: ['', [Validators.required, futureDateValidator()]]
  });

  ngOnInit() {
    this.isAdmin.set(LocalStorageUtils.getRoleFromToken() === 'ADMIN');
    const idString = LocalStorageUtils.getIDFromToken();
    this.currentUserId = idString ? Number(idString) : null;

    this.loadStatusTypes();
    this.loadProjects();
    this.getTaskToEdit();
  }

  // la schimbarea proiectului, incarca membrii lui in dropdown-ul de assignee
  onProjectChange() {
    const projectId = this.taskForm.controls.projectId.value;

    this.taskForm.patchValue({ userId: 0 });
    this.users.set([]);
    this.errorUsers.set(null);

    if (!projectId || projectId < 1) {
      return;
    }

    this.loadMembers(projectId);
  }

  private loadMembers(projectId: number, selectUserId?: number) {
    this.isLoadingUsers.set(true);
    this.errorUsers.set(null);
    this.serviceProject.getProjectMembers(projectId).subscribe({
      next: (data) => {
        this.users.set(data);
        this.isLoadingUsers.set(false);

        const preselect = selectUserId ?? this.preselectedUserId;
        if (preselect !== undefined && data.some(u => u.userId === Number(preselect))) {
          this.taskForm.patchValue({ userId: Number(preselect) });
        }
      },
      error: (err) => {
        this.errorUsers.set('Eroare la încărcarea membrilor proiectului.');
        this.isLoadingUsers.set(false);
        console.error(err);
      }
    });
  }

  getTaskToEdit() {

    if (this.id !== undefined) {
      this.serviceTask.getTaskById(this.id).subscribe({
        next: (data) => {
          this.taskToEdit.set(data);

          this.taskForm.patchValue({
            taskName: data.taskName,
            statusTypeId: data.statusTypeId,
            projectId: data.projectId,
            dueDate: data.dueDate
          });

          if (data.projectId) {
            this.loadMembers(data.projectId, data.userId);
          }
        },
        error: (err) => {
          console.error('Eroare la încărcarea taskului:', err);

        }
      });

    }
  }
  onSubmitTaskForm() {
    if (this.taskForm.invalid) return;

    const formValue = this.taskForm.getRawValue() as TaskCreateDTO;

    this.isSaving.set(true);

    if (this.taskToEdit()) {
      this.serviceTask.updateTask(this.taskToEdit()!.taskId, formValue).subscribe({
        next: () => {
          this.isSaving.set(false);
          alert('Task actualizat cu succes!');
          this.router.navigate(["/my-tasks"]);
        },
        error: (err) => {
          this.isSaving.set(false);
          console.error('Eroare la actualizare:', err);
        }
      });


    } else {

      this.serviceTask.addTask(formValue).subscribe({
        next: () => {
          this.isSaving.set(false);
          alert('Task adăugat cu succes!');
          this.taskForm.reset();
        },
        error: (err) => {
          this.isSaving.set(false);
          console.error('Eroare la salvare:', err);
        }
      });
    }
  }

  private loadStatusTypes() {
    this.isLoadingStatusTypes.set(true);
    this.errorStatusTypes.set(null);
    this.serviceStatusType.getStatusTypes().subscribe({
      next: (data) => {
        this.statusTypes.set(data);
        this.isLoadingStatusTypes.set(false);
      },
      error: (err) => {
        this.errorStatusTypes.set('Eroare la încărcarea statusurilor.');
        this.isLoadingStatusTypes.set(false);
        console.error(err);
      }
    });
  }

  private loadProjects() {
    this.isLoadingProjects.set(true);
    this.errorProjects.set("");

    // adminul vede toate proiectele; un user vede doar proiectele din care face parte
    const projects$ = this.isAdmin()
      ? this.serviceProject.getProjects()
      : (this.currentUserId !== null
        ? this.serviceProject.getProjectsByMember(this.currentUserId)
        : null);

    if (!projects$) {
      this.errorProjects.set('User ID not found');
      this.isLoadingProjects.set(false);
      return;
    }

    projects$.subscribe({
      next: (data) => {
        this.projects.set(data);
        this.isLoadingProjects.set(false);
      },
      error: (err) => {
        this.errorProjects.set('Eroare la încărcarea proiectelor.');
        this.isLoadingProjects.set(false);
        console.error(err);
      }
    });

  }

  onCancel() {
    this.router.navigate(["/my-tasks"]);
  }

}




export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) return null;
    const inputDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (inputDate.getTime() < today.getTime()) return { futureDate: true };
    return null;
  };
}