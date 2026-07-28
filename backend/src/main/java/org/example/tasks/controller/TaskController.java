package org.example.tasks.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.TaskCreateDTO;
import org.example.tasks.dto.request.TaskFilterDTO;
import org.example.tasks.dto.response.TaskDTO;
import org.example.tasks.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@CrossOrigin
public class TaskController {

    private final TaskService taskService;


    @GetMapping
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'get all')")
    public List<TaskDTO> getTasks() {
        return taskService.getTasks();
    }

    @PostMapping
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'create')")
    public List<TaskDTO> createTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO) {
        return taskService.addTask(taskCreateDTO);
    }

    @PostMapping("/list")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'create')")
    public List<TaskDTO> createTasks(@RequestBody @Valid List<TaskCreateDTO> taskCreateDTOList) {
        return taskService.addTasks(taskCreateDTOList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'create')")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'update')")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO, @PathVariable Long id) {
        TaskDTO task = taskService.updateTaskById(id, taskCreateDTO);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'delete')")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'filter')")
    public List<TaskDTO> filterTasks(
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String statusName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateTime
    ) {
        TaskFilterDTO filter = TaskFilterDTO.builder()
                .taskName(taskName)
                .statusName(statusName)
                .username(username)
                .dueDateTime(dueDateTime)
                .build();

        return taskService.filterTasks(filter);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'update')")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestParam String statusTypeId) {
        TaskDTO task = taskService.updateStatus(id, statusTypeId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/user")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'update')")
    public ResponseEntity<TaskDTO> updateTaskUser(@PathVariable Long id,
                                                  @RequestParam(required = false) Long userId) {
        TaskDTO task = taskService.updateUser(id, userId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/due-date-time")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'update')")
    public ResponseEntity<TaskDTO> updateDueDateTime(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateTime) {
        TaskDTO task = taskService.updateDueDateTime(id, dueDateTime);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/count")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'get all')")
    public long countTasks(@RequestParam(required = false) String statusName) {
        return taskService.countTasks(statusName);
    }

    @GetMapping("/overdue")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'get all')")
    public List<TaskDTO> getOverdueTasks() {
        return taskService.getOverdueTasks();
    }

    @GetMapping("/due-between")
    @PreAuthorize("@permissionChecker.checkPermission('tasks', 'get all')")
    public List<TaskDTO> getTasksDueBetween(@RequestParam LocalDate start,
                                         @RequestParam LocalDate end) {
        return taskService.getTasksDueBetween(start, end);
    }
}
