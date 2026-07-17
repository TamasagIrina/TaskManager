import { Component} from '@angular/core';

import { AddEditTaskComponent } from "../../shared/add-edit-task/add-edit-task.component";

@Component({
  selector: 'app-new-task',
  imports: [AddEditTaskComponent],
  templateUrl: './new-task.component.html',
  styleUrl: './new-task.component.css'
})
export class NewTaskComponent  {

}
