import { Component, inject, signal } from '@angular/core';
import { ProjectDTO } from '../../../domains/ProjectDTOResponse';
import { ServiceProjectService } from '../../../services/service-project.service';
import { ProjectCardComponent } from '../../shared/project-card/project-card.component';
import { LoadingComponent } from '../../shared/loading/loading.component';
import LocalStorageUtils from '../../../utils/localStorageUtils';

@Component({
  selector: 'app-my-projects',
  imports: [ProjectCardComponent, LoadingComponent],
  templateUrl: './my-projects.component.html',
  styleUrl: './my-projects.component.css'
})
export class MyProjectsComponent {

  private serviceProject = inject(ServiceProjectService);

  projects = signal<ProjectDTO[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  ngOnInit() {
    this.getMyProjects();
  }

  getMyProjects() {
    const idString = LocalStorageUtils.getIDFromToken();
    if (!idString) {
      this.error.set('User ID not found');
      return;
    }

    const userId = Number(idString);
    if (Number.isNaN(userId)) {
      this.error.set('Invalid user ID');
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.serviceProject.getProjectsByMember(userId).subscribe({
      next: (data) => {
        this.projects.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Eroare la încărcarea proiectelor.');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  updateProjectInList(updated: ProjectDTO) {
    this.projects.update(current =>
      current.map(p => p.projectId === updated.projectId ? updated : p)
    );
  }
}
