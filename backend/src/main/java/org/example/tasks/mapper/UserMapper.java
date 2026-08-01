package org.example.tasks.mapper;

import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    // Entitate -> DTO de raspuns
    public UserDTO toDTO(User user) {
        UserDTO.UserDTOBuilder builder = UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .isInternal(user.getIsInternal())
                .createdBy(user.getCreatedBy())
                .creationDate(user.getCreationDate());

        if (user.getRole() != null) {
            builder.roleId(user.getRole().getRoleId())
                    .roleName(user.getRole().getRoleName());
        }

        return builder.build();
    }

    // DTO de request -> Entitate
    public User toEntity(UserCreateDTO userCreateDTO) {
        return User.builder()
                .username(userCreateDTO.getUsername())
                .email(userCreateDTO.getEmail())
                .password(userCreateDTO.getPassword())
                .birthDate(userCreateDTO.getBirthDate())
                .build();

    }
}
