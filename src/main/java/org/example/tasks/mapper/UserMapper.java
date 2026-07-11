package org.example.tasks.mapper;

import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    // Entitate -> DTO de raspuns (ce trimitem catre client)
    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .birthDate(user.getBirthDate())
                .isInternal(user.getIsInternal())
                .createdBy(user.getCreatedBy())
                .creationDate(user.getCreationDate())
                .build();
    }

    // DTO de request -> Entitate (ce primim de la client)
    public User toEntity(UserCreateDTO userCreateDTO) {
        return User.builder()
                .username(userCreateDTO.getUsername())
                .birthDate(userCreateDTO.getBirthDate())
                .isInternal(userCreateDTO.getIsInternal())
                .build();

    }
}
