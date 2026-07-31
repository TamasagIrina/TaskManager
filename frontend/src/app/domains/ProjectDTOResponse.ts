export interface ProjectDTO {
  projectId: number;
  projectName: string;
  projectDescription: string;
  statusTypeId: string;
  statusName: string;
  createdBy: string;
  creationDate: string | Date; 
  lastUpdateDate: string | Date;
  memberIds: number[];
}