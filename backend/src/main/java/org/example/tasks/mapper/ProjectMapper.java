package org.example.tasks.mapper;

import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.ProjectCreateDTO;
import org.example.tasks.dto.response.ProjectDTO;
import org.example.tasks.model.Project;
import org.example.tasks.model.StatusType;
import org.example.tasks.model.User;
import org.example.tasks.repository.StatusTypeRepository;
import org.example.tasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectMapper {
    private final StatusTypeRepository statusTypeRepository;
    private final UserRepository userRepository;

    public ProjectDTO toDTO(Project project) {
        ProjectDTO.ProjectDTOBuilder builder = ProjectDTO.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .createdBy(project.getCreatedBy())
                .creationDate(project.getCreationDate())
                .lastUpdateDate(project.getLastUpdateDate());

        if (project.getStatusType() != null) {
            builder.statusTypeId(project.getStatusType().getStatusTypeId())
                    .statusName(project.getStatusType().getStatusName());
        }

        if (project.getMembers() != null) {
            builder.memberIds(project.getMembers().stream().map(User::getUserId).toList());
        }

        return builder.build();
    }

    public Project toEntity(ProjectCreateDTO dto) {
        return Project.builder()
                .projectName(dto.getProjectName())
                .projectDescription(dto.getProjectDescription())
                .statusType(resolveStatusType(dto.getStatusTypeId()))
                .members(resolveMembers(dto.getMemberIds()))
                .build();
    }

    public void updateEntity(Project project, ProjectCreateDTO dto) {
        project.setProjectName(dto.getProjectName());
        project.setProjectDescription(dto.getProjectDescription());
        project.setStatusType(resolveStatusType(dto.getStatusTypeId()));
        project.setMembers(resolveMembers(dto.getMemberIds()));
    }

    private StatusType resolveStatusType(String statusTypeId) {
        if (statusTypeId == null) {
            return null;
        }
        return statusTypeRepository.findById(statusTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a fost gasit niciun status cu id-ul: " + statusTypeId));
    }

    private List<User> resolveMembers(List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return List.of();
        }
        List<User> users = userRepository.findAllById(memberIds);
        if (users.size() != memberIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unul sau mai mulți useri nu au fost găsiți");
        }
        return users;
    }
}
