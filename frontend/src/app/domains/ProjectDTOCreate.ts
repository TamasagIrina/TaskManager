export interface ProjectCreateDTO {
  projectName: string;
  projectDescription: string;
  statusTypeId: string;
  memberIds: number[];
}
