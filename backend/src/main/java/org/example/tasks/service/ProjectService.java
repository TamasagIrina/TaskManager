package org.example.tasks.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.ProjectCreateDTO;
import org.example.tasks.dto.response.ProjectDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.mapper.ProjectMapper;
import org.example.tasks.mapper.UserMapper;
import org.example.tasks.model.Project;
import org.example.tasks.model.StatusType;
import org.example.tasks.model.User;
import org.example.tasks.repository.ProjectRepository;
import org.example.tasks.repository.StatusTypeRepository;
import org.example.tasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final StatusTypeRepository statusTypeRepository;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toDTO)
                .toList();
    }

    public List<ProjectDTO> getProjectsByMember(Long userId) {
        return projectRepository.findByMemberId(userId).stream()
                .map(projectMapper::toDTO)
                .toList();
    }

    @Transactional
    public List<UserDTO> getProjectMembers(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project not found with id: " + id));

        if (project.getMembers() == null) {
            return List.of();
        }

        return project.getMembers().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public ProjectDTO getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(projectMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project not found with id: " + id));
    }

    @Transactional
    public ProjectDTO createProject(ProjectCreateDTO dto) {
        Project project = projectMapper.toEntity(dto);
        Project saved = projectRepository.save(project);

        if (saved.getMembers() != null) {
            saved.getMembers().forEach(member -> notificationService.notifyNewProject(member, saved));
        }

        return projectMapper.toDTO(saved);
    }

    @Transactional
    public ProjectDTO updateProject(Long id, ProjectCreateDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project not found with id: " + id));

        projectMapper.updateEntity(project, dto);

        Project saved = projectRepository.save(project);

        if (saved.getMembers() != null) {
            saved.getMembers().forEach(member -> notificationService.notifyProjectUpdated(member, saved));
        }

        return projectMapper.toDTO(saved);
    }

    @Transactional
    public ProjectDTO addMembers(Long id, List<Long> memberIds) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project not found with id: " + id));

        if (memberIds == null || memberIds.isEmpty()) {
            return projectMapper.toDTO(project);
        }

        List<User> toAdd = userRepository.findAllById(memberIds);
        if (toAdd.size() != memberIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unul sau mai mulți useri nu au fost găsiți");
        }

        List<User> members = project.getMembers() != null
                ? new ArrayList<>(project.getMembers())
                : new ArrayList<>();

        List<User> newlyAdded = new ArrayList<>();
        for (User user : toAdd) {
            boolean alreadyMember = members.stream()
                    .anyMatch(m -> m.getUserId().equals(user.getUserId()));
            if (!alreadyMember) {
                members.add(user);
                newlyAdded.add(user);
            }
        }

        project.setMembers(members);
        Project saved = projectRepository.save(project);

        newlyAdded.forEach(member -> notificationService.notifyAddedToProject(member, saved));

        return projectMapper.toDTO(saved);
    }

    @Transactional
    public ProjectDTO updateStatus(Long id, String statusTypeId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project not found with id: " + id));

        StatusType statusType = statusTypeRepository.findById(statusTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a fost gasit niciun status cu id-ul: " + statusTypeId));

        project.setStatusType(statusType);
        Project saved = projectRepository.save(project);
        return projectMapper.toDTO(saved);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }
}
