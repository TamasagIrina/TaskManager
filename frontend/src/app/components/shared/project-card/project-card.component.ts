import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { ProjectDTO } from '../../../domains/ProjectDTOResponse';
import { StatusTypeDTO } from '../../../domains/StatusTypeDTO';
import { ProjectMembersModalComponent } from '../project-members-modal/project-members-modal.component';
import { ServiceStatusTypeService } from '../../../services/service-status-type.service';
import { ServiceProjectService } from '../../../services/service-project.service';
import LocalStorageUtils from '../../../utils/localStorageUtils';

@Component({
  selector: 'app-project-card',
  imports: [MatIcon, FormsModule, ProjectMembersModalComponent],
  templateUrl: './project-card.component.html',
  styleUrl: './project-card.component.css'
})
export class ProjectCardComponent {

  @Input({ required: true }) project!: ProjectDTO;

  @Output() deleteProjectEvent = new EventEmitter<ProjectDTO>();
  @Output() projectUpdatedEvent = new EventEmitter<ProjectDTO>();

  private router = inject(Router);
  private serviceStatusType = inject(ServiceStatusTypeService);
  private serviceProject = inject(ServiceProjectService);

  showMembersModal = signal<boolean>(false);
  isAdmin = signal<boolean>(false);

  statusTypes = signal<StatusTypeDTO[]>([]);

  ngOnInit() {
    this.isAdmin.set(LocalStorageUtils.getRoleFromToken() === 'ADMIN');
  }
  editingStatus = signal<boolean>(false);
  selectedStatusId = signal<string>('');
  savingStatus = signal<boolean>(false);

  toggleEditStatus() {
    if (!this.editingStatus()) {
      this.selectedStatusId.set(this.project.statusTypeId ?? '');
      if (this.statusTypes().length === 0) {
        this.loadStatusTypes();
      }
    }
    this.editingStatus.update(v => !v);
  }

  private loadStatusTypes() {
    this.serviceStatusType.getStatusTypes().subscribe({
      next: (data) => this.statusTypes.set(data),
      error: (err) => console.error('Eroare la încărcarea statusurilor:', err)
    });
  }

  saveStatus() {
    const statusId = this.selectedStatusId();
    if (!statusId) {
      return;
    }

    this.savingStatus.set(true);
    this.serviceProject.updateProjectStatus(this.project.projectId, statusId).subscribe({
      next: (updated) => {
        this.project = updated;
        this.savingStatus.set(false);
        this.editingStatus.set(false);
        this.projectUpdatedEvent.emit(updated);
      },
      error: (err) => {
        this.savingStatus.set(false);
        console.error('Eroare la actualizarea statusului:', err);
      }
    });
  }

  openMembersModal() {
    this.showMembersModal.set(true);
  }

  closeMembersModal() {
    this.showMembersModal.set(false);
  }

  onMembersAdded(updated: ProjectDTO) {
    this.project = updated;
    this.showMembersModal.set(false);
    this.projectUpdatedEvent.emit(updated);
  }

  openEditProject() {
    this.router.navigate(['/edit-project', this.project.projectId], {
      state: { id: this.project.projectId }
    });
  }

  onDeleteClick() {
    this.deleteProjectEvent.emit(this.project);
  }
}
