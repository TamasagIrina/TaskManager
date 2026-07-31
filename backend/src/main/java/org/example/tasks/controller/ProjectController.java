package org.example.tasks.controller;

import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.ProjectCreateDTO;
import org.example.tasks.dto.response.ProjectDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @PreAuthorize("@permissionChecker.checkPermission('projects', 'get all')")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionChecker.checkPermission('projects', 'get by id')")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("@permissionChecker.isSelfOrAdmin(#id)")
    public ResponseEntity<List<ProjectDTO>> getProjectsByMember(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectsByMember(id));
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("@permissionChecker.isProjectMemberOrAdmin(#id)")
    public ResponseEntity<List<UserDTO>> getProjectMembers(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectMembers(id));
    }

    @PostMapping
    @PreAuthorize("@permissionChecker.checkPermission('projects', 'create')")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectCreateDTO dto) {
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionChecker.isAdmin()")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectCreateDTO dto) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    @PatchMapping("/{id}/members")
    @PreAuthorize("@permissionChecker.isAdmin()")
    public ResponseEntity<ProjectDTO> addMembers(@PathVariable Long id, @RequestBody List<Long> memberIds) {
        return ResponseEntity.ok(projectService.addMembers(id, memberIds));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@permissionChecker.isProjectMemberOrAdmin(#id)")
    public ResponseEntity<ProjectDTO> updateStatus(@PathVariable Long id, @RequestParam String statusTypeId) {
        return ResponseEntity.ok(projectService.updateStatus(id, statusTypeId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionChecker.checkPermission('projects', 'delete')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
