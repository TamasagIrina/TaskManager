import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ServiceUserService } from '../../../services/service-user.service';
import { UserDTOResponse } from '../../../domains/UserDTOResponse';
import { LoadingComponent } from '../loading/loading.component';

@Component({
  selector: 'app-reassing-task-delete-user-modal',
  imports: [FormsModule, LoadingComponent],
  templateUrl: './reassing-task-delete-user-modal.component.html',
  styleUrl: './reassing-task-delete-user-modal.component.css'
})
export class ReassingTaskDeleteUserModalComponent {

  @Input({ required: true }) userId!: number;
  @Input() username = '';

  @Output() closeModal = new EventEmitter<void>();
  @Output() reassigned = new EventEmitter<number>();

  private serviceUser = inject(ServiceUserService);

  users = signal<UserDTOResponse[]>([]);
  selectedTargetId = signal<number | null>(null);

  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading.set(true);
    this.error.set(null);
    this.serviceUser.getUsers().subscribe({
      next: (resp) => {
        // nu poti reasigna task-urile catre userul care e sters
        this.users.set(resp.filter(u => u.userId !== this.userId));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Eroare la încărcarea utilizatorilor.');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  confirm() {
    const target = this.selectedTargetId();
    if (target === null) {
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.serviceUser.reassignAndDeleteUser(this.userId, target).subscribe({
      next: () => {
        this.saving.set(false);
        this.reassigned.emit(this.userId);
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set('Reasignarea a eșuat. Încearcă din nou.');
        console.error(err);
      }
    });
  }

  onClose() {
    this.closeModal.emit();
  }
}
