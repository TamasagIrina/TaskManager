import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AddEditTaskComponent } from "../add-edit-task/add-edit-task.component";

@Component({
  selector: 'app-edit-task',
  imports: [AddEditTaskComponent],
  templateUrl: './edit-task.component.html',
  styleUrl: './edit-task.component.css'
})
export class EditTaskComponent implements OnInit {

  router= inject(Router);

  id= this.router.getCurrentNavigation()?.extras.state?.['id'];

  ngOnInit() {
    console.log('Edit Task ID:', this.id);
  }

}
