export interface TaskCreateDTO {
  taskName: string;
  statusTypeId: string;
  projectId: number;
  userId: number;
  dueDate: string;
}