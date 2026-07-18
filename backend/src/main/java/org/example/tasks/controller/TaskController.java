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
    public List<TaskDTO> getTasks() {
        return taskService.getTasks();
    }

    @PostMapping
    public List<TaskDTO> createTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO) {
        return taskService.addTask(taskCreateDTO);
    }

    @PostMapping("/list")
    public List<TaskDTO> createTasks(@RequestBody @Valid List<TaskCreateDTO> taskCreateDTOList) {
        return taskService.addTasks(taskCreateDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO, @PathVariable Long id) {
        TaskDTO task = taskService.updateTaskById(id, taskCreateDTO);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
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
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestParam String statusTypeId) {
        TaskDTO task = taskService.updateStatus(id, statusTypeId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/user")
    public ResponseEntity<TaskDTO> updateTaskUser(@PathVariable Long id,
                                                  @RequestParam(required = false) Long userId) {
        TaskDTO task = taskService.updateUser(id, userId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/due-date-time")
    public ResponseEntity<TaskDTO> updateDueDateTime(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateTime) {
        TaskDTO task = taskService.updateDueDateTime(id, dueDateTime);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/count")
    public long countTasks(@RequestParam(required = false) String statusName) {
        return taskService.countTasks(statusName);
    }

    @GetMapping("/overdue")
    public List<TaskDTO> getOverdueTasks() {
        return taskService.getOverdueTasks();
    }

    @GetMapping("/due-between")
    public List<TaskDTO> getTasksDueBetween(@RequestParam LocalDate start,
                                         @RequestParam LocalDate end) {
        return taskService.getTasksDueBetween(start, end);
    }
}
