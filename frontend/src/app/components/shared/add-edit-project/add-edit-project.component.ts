import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ServiceProjectService } from '../../../services/service-project.service';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { ServiceUserService } from '../../../services/service-user.service';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { UserDTOResponse } from '../../../domains/UserDTOResponse';
import { ProjectCreateDTO } from '../../../domains/ProjectDTOCreate';
import { LoadingComponent } from '../loading/loading.component';

@Component({
  selector: 'app-add-edit-project',
  imports: [ReactiveFormsModule, LoadingComponent],
  templateUrl: './add-edit-project.component.html',
  styleUrl: './add-edit-project.component.css'
})
export class AddEditProjectComponent {

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private serviceProject = inject(ServiceProjectService);
  private serviceStatusType = inject(ServiceStatusTypeService);
  private serviceUser = inject(ServiceUserService);

  projectId = signal<number | null>(null);
  isEditMode = signal<boolean>(false);

  statusTypes = signal<StatusTypeDTO[]>([]);
  users = signal<UserDTOResponse[]>([]);

  selectedMemberIds = signal<number[]>([]);

  isLoadingStatusTypes = signal(false);
  errorStatusTypes = signal<string | null>(null);

  isLoadingUsers = signal(false);
  errorUsers = signal<string | null>(null);

  isSaving = signal(false);

  projectForm = this.fb.nonNullable.group({
    projectName: ['', [Validators.required, Validators.maxLength(500)]],
    projectDescription: ['', [Validators.maxLength(4000)]],
    statusTypeId: ['', [Validators.required, Validators.maxLength(255)]]
  });

  ngOnInit() {
    this.loadStatusTypes();
    this.loadUsers();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam !== null) {
      const id = Number(idParam);
      if (!Number.isNaN(id)) {
        this.projectId.set(id);
        this.isEditMode.set(true);
        this.loadProject(id);
      }
    }
  }

  private loadProject(id: number) {
    this.serviceProject.getProjectById(id).subscribe({
      next: (project) => {
        this.projectForm.patchValue({
          projectName: project.projectName,
          projectDescription: project.projectDescription ?? '',
          statusTypeId: project.statusTypeId ?? ''
        });
        this.selectedMemberIds.set(project.memberIds ?? []);
      },
      error: (err) => {
        console.error('Eroare la încărcarea proiectului:', err);
      }
    });
  }

  toggleMember(userId: number) {
    this.selectedMemberIds.update(ids =>
      ids.includes(userId) ? ids.filter(id => id !== userId) : [...ids, userId]
    );
  }

  isMemberSelected(userId: number): boolean {
    return this.selectedMemberIds().includes(userId);
  }

  onSubmitProjectForm() {
    if (this.projectForm.invalid) return;

    const formValue = this.projectForm.getRawValue();

    const project: ProjectCreateDTO = {
      projectName: formValue.projectName,
      projectDescription: formValue.projectDescription,
      statusTypeId: formValue.statusTypeId,
      memberIds: this.selectedMemberIds()
    };

    this.isSaving.set(true);

    const id = this.projectId();

    if (this.isEditMode() && id !== null) {
      this.serviceProject.updateProject(id, project).subscribe({
        next: () => {
          this.isSaving.set(false);
          alert('Project actualizat cu succes!');
          this.router.navigate(['/all-projects']);
        },
        error: (err) => {
          this.isSaving.set(false);
          console.error('Eroare la salvare:', err);
        }
      });
    } else {
      this.serviceProject.createProject(project).subscribe({
        next: () => {
          this.isSaving.set(false);
          alert('Project adăugat cu succes!');
          this.router.navigate(['/admin-dashboard']);
        },
        error: (err) => {
          this.isSaving.set(false);
          console.error('Eroare la salvare:', err);
        }
      });
    }
  }

  onCancel() {
    this.router.navigate([this.isEditMode() ? '/all-projects' : '/admin-dashboard']);
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
}
