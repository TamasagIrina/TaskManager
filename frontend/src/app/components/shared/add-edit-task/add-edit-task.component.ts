import { Component, inject, Input, input, output, signal } from '@angular/core';
import { TaskDTOResponse } from '../../../domains/TaskDTOResponse';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ServiceTasksService } from '../../../services/service-tasks.service';
import { ServiceUserService } from '../../../services/service-user.service';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { UserDTOResponse } from '../../../domains/UserDTOResponse';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { TaskCreateDTO } from '../../../domains/TaskDTOCreate';
import { LoadingComponent } from "../loading/loading.component";
import { Router } from '@angular/router';

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


  private fb = inject(FormBuilder);
  private serviceTask = inject(ServiceTasksService);
  private serviceUser = inject(ServiceUserService);
  private serviceStatusType = inject(ServiceStatusTypeService);

  users = signal<UserDTOResponse[]>([]);
  statusTypes = signal<StatusTypeDTO[]>([]);

  isLoadingUsers = signal(false);
  errorUsers = signal<string | null>(null);

  isLoadingStatusTypes = signal(false);
  errorStatusTypes = signal<string | null>(null);

  taskForm = this.fb.nonNullable.group({
    taskName: ['', [Validators.required, Validators.maxLength(500)]],
    statusTypeId: ['', [Validators.required, Validators.maxLength(255)]],
    userId: [0, Validators.required],
    dueDate: ['', [Validators.required, futureDateValidator()]]
  });

  ngOnInit() {
    this.loadUsers();
    this.loadStatusTypes();
    this.getTaskToEdit();
  }

  getTaskToEdit() {

    if (this.id!==undefined) {
      this.serviceTask.getTaskById(this.id).subscribe({
        next: (data) => {
          this.taskToEdit.set(data);

          this.taskForm.patchValue({
            taskName: data.taskName,
            statusTypeId: data.statusTypeId,
            userId: data.userId,
            dueDate: data.dueDate
          });
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


    if (this.taskToEdit()) {
      this.serviceTask.updateTask(this.taskToEdit()!.taskId, formValue).subscribe({
        next: () => {
          console.log('Task actualizat cu succes!');
          alert('Task actualizat cu succes!');
          
          this.router.navigate(["/my-tasks"]);


        },
        error: (err) => {
          console.error('Eroare la actualizare:', err);
        }
      });


    } else {

      this.serviceTask.addTask(formValue).subscribe({
        next: () => {
          console.log('Task adăugat cu succes!');
          alert('Task adăugat cu succes!');
          this.taskForm.reset();

        },
        error: (err) => {
          console.error('Eroare la salvare:', err);
        }
      });
    }
  }

  private loadUsers() {
    this.isLoadingUsers.set(true);
    this.errorUsers.set(null);
    this.serviceUser.getUsers().subscribe({
      next: (data) => {
        this.users.set(data);
        this.isLoadingUsers.set(false);
      },
      error: (err) => {
        this.errorUsers.set('Eroare la încărcarea utilizatorilor.');
        this.isLoadingUsers.set(false);
        console.error(err);
      }
    });
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