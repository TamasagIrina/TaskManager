package org.example.tasks.mapper;

import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.TaskCreateDTO;
import org.example.tasks.dto.response.TaskDTO;
import org.example.tasks.model.StatusType;
import org.example.tasks.model.Task;
import org.example.tasks.model.User;
import org.example.tasks.repository.StatusTypeRepository;
import org.example.tasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final StatusTypeRepository statusTypeRepository;
    private final UserRepository userRepository;

    // Entitate -> DTO de raspuns
    public TaskDTO toDTO(Task task) {
        TaskDTO.TaskDTOBuilder builder = TaskDTO.builder()
                .taskId(task.getTaskId())
                .taskName(task.getTaskName())
                .dueDate(task.getDueDate())
                .createdBy(task.getCreatedBy())
                .creationDate(task.getCreationDate())
                .lastUpdateDate(task.getLastUpdateDate());

        if (task.getStatusType() != null) {
            builder.statusTypeId(task.getStatusType().getStatusTypeId())
                    .statusName(task.getStatusType().getStatusName());
        }

        if (task.getUser() != null) {
            builder.userId(task.getUser().getUserId())
                    .username(task.getUser().getUsername());
        }

        return builder.build();
    }

    // DTO de request -> Entitate
    public Task toEntity(TaskCreateDTO taskCreateDTO) {
        return Task.builder()
                .taskName(taskCreateDTO.getTaskName())
                .statusType(resolveStatusType(taskCreateDTO.getStatusTypeId()))
                .user(resolveUser(taskCreateDTO.getUserId()))
                .dueDate(taskCreateDTO.getDueDate())
                .build();
    }


    public StatusType resolveStatusType(String statusTypeId) {
        if (statusTypeId == null) {
            return null;
        }
        return statusTypeRepository.findById(statusTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a fost gasit niciun status cu id-ul: " + statusTypeId));
    }


    public User resolveUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a fost gasit niciun user cu id-ul: " + userId));
    }
}
