import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { ProjectDTO } from '../../../domains/ProjectDTOResponse';
import { UserDTOResponse } from '../../../domains/UserDTOResponse';
import { ServiceUserService } from '../../../services/service-user.service';
import { ServiceProjectService } from '../../../services/service-project.service';
import { LoadingComponent } from '../loading/loading.component';

@Component({
  selector: 'app-project-members-modal',
  imports: [LoadingComponent],
  templateUrl: './project-members-modal.component.html',
  styleUrl: './project-members-modal.component.css'
})
export class ProjectMembersModalComponent {

  @Input({ required: true }) project!: ProjectDTO;
  @Output() closeModal = new EventEmitter<void>();
  @Output() membersAdded = new EventEmitter<ProjectDTO>();

  private serviceUser = inject(ServiceUserService);
  private serviceProject = inject(ServiceProjectService);

  users = signal<UserDTOResponse[]>([]);
  selectedIds = signal<number[]>([]);

  isLoadingUsers = signal(false);
  errorUsers = signal<string | null>(null);
  saving = signal(false);

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoadingUsers.set(true);
    this.errorUsers.set(null);
    this.serviceUser.getUsers().subscribe({
      next: (data) => {
        const existing = this.project.memberIds ?? [];
        this.users.set(data.filter(u => !existing.includes(u.userId)));
        this.isLoadingUsers.set(false);
      },
      error: (err) => {
        this.errorUsers.set('Eroare la încărcarea utilizatorilor.');
        this.isLoadingUsers.set(false);
        console.error(err);
      }
    });
  }

  toggle(userId: number) {
    this.selectedIds.update(ids =>
      ids.includes(userId) ? ids.filter(id => id !== userId) : [...ids, userId]
    );
  }

  isSelected(userId: number): boolean {
    return this.selectedIds().includes(userId);
  }

  onAdd() {
    if (this.selectedIds().length === 0) {
      return;
    }

    this.saving.set(true);
    this.serviceProject.addMembers(this.project.projectId, this.selectedIds()).subscribe({
      next: (updated) => {
        this.saving.set(false);
        this.membersAdded.emit(updated);
      },
      error: (err) => {
        this.saving.set(false);
        console.error('Eroare la adăugarea membrilor:', err);
      }
    });
  }

  onClose() {
    this.closeModal.emit();
  }
}
