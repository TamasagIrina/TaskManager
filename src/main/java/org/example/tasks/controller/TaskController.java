package org.example.tasks.controller;

import jakarta.validation.Valid;
import org.example.tasks.dto.request.TaskCreateDTO;
import org.example.tasks.dto.request.TaskFilterDTO;
import org.example.tasks.dto.response.TaskDTO;
import org.example.tasks.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDTO> getTasks() {
        return taskService.getTasks();
    }

    @PostMapping
    public List<TaskDTO> createTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO) {
        return taskService.addTask(taskCreateDTO);
    }

    @PostMapping("/list")
    public List<TaskDTO> createTasks(@RequestBody  @Valid List<TaskCreateDTO>  taskCreateDTOList) {
        return taskService.addTasks(taskCreateDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById( @PathVariable Long id) {

        try {
            TaskDTO task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO, @PathVariable Long id) {
        try {
            TaskDTO task = taskService.updateTaskById(id, taskCreateDTO);
            return ResponseEntity.ok(task);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskDTO> deleteTaskById(@PathVariable Long id) {
        try {
            taskService.deleteTaskById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/filter")
    public List<TaskDTO> filterTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateTime
    ) {
        TaskFilterDTO filter = TaskFilterDTO.builder()
                .status(status)
                .content(content)
                .dueDateTime(dueDateTime)
                .build();

        return taskService.filterTasks(filter);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            TaskDTO task = taskService.updateStatus(id, status);
            return ResponseEntity.ok(task);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/due-date-time")
    public ResponseEntity<TaskDTO> updateDueDateTime(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateTime) {
        try {
            TaskDTO task = taskService.updateDueDateTime(id, dueDateTime);
            return ResponseEntity.ok(task);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count")
    public long countTasks(@RequestParam(required = false) String status) {
        return taskService.countTasks(status);
    }

    @GetMapping("/overdue")
    public List<TaskDTO> getOverdueTasks() {
        return taskService.getOverdueTasks();
    }
}
