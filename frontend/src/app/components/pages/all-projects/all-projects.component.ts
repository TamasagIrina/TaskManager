import { Component, inject, signal } from '@angular/core';
import { ProjectDTO } from '../../../domains/ProjectDTOResponse';
import { ServiceProjectService } from '../../../services/service-project.service';
import { ProjectCardComponent } from '../../shared/project-card/project-card.component';
import { LoadingComponent } from '../../shared/loading/loading.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-projects',
  imports: [ProjectCardComponent, LoadingComponent],
  templateUrl: './all-projects.component.html',
  styleUrl: './all-projects.component.css'
})
export class AllProjectsComponent {

  private serviceProject = inject(ServiceProjectService);
  private router = inject(Router);

  projects = signal<ProjectDTO[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  ngOnInit() {
    this.getProjects();
  }

  getProjects() {
    this.loading.set(true);
    this.error.set(null);
    this.serviceProject.getProjects().subscribe({
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

  goToNewProject() {
    this.router.navigate(['/new-project']);
  }

  deleteProjectFromList(project: ProjectDTO) {
    const isConfirmed = window.confirm(
      `Are you sure you want to delete the project "${project.projectName}"? This action cannot be undone.`
    );

    if (!isConfirmed) {
      return;
    }

    this.serviceProject.deleteProject(project.projectId).subscribe({
      next: () => {
        alert(`Project "${project.projectName}" has been deleted.`);
        this.projects.update(current =>
          current.filter(p => p.projectId !== project.projectId)
        );
      },
      error: (err) => {
        console.error(`Error deleting project "${project.projectName}":`, err);
        alert(`Failed to delete project "${project.projectName}". Please try again later.`);
      }
    });
  }

  updateProjectInList(updated: ProjectDTO) {
    this.projects.update(current =>
      current.map(p => p.projectId === updated.projectId ? updated : p)
    );
  }
}
