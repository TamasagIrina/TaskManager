import { UserDTOResponse } from "./UserDTOResponse";

export interface AuthResponseDTO {
    user:UserDTOResponse;
    message:string;
}