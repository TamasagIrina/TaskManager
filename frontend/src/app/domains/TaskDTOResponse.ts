export interface TaskDTOResponse {
  taskId: number;
  taskName: string;

  statusTypeId: string;
  statusName: string;

  userId: number;
  username: string;

  dueDate: string;         
  creationDate: string;   
  createdBy: string;
  lastUpdateDate: string;
}