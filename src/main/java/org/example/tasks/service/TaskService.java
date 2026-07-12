package org.example.tasks.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.request.TaskCreateDTO;
import org.example.tasks.dto.request.TaskFilterDTO;
import org.example.tasks.dto.response.TaskDTO;
import org.example.tasks.mapper.TaskMapper;
import org.example.tasks.model.StatusType;
import org.example.tasks.model.Task;
import org.example.tasks.model.User;
import org.example.tasks.repository.StatusTypeRepository;
import org.example.tasks.repository.TaskRepository;
import org.example.tasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final StatusTypeRepository statusTypeRepository;
    private final TaskMapper taskMapper;

    public List<TaskDTO> getTasks() {
        log.info("Getting Tasks: ");

        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Transactional
    public List<TaskDTO> addTask(TaskCreateDTO taskCreateDTO) {
        Task newTask = taskMapper.toEntity(taskCreateDTO);

        //aici se vor seta createdBy si createdByFullName din token

        taskRepository.save(newTask);

        log.info("Added Task: {} ", newTask);

        return getTasks();
    }

    @Transactional
    public List<TaskDTO> addTasks(List<TaskCreateDTO> taskDTO) {
        for (TaskCreateDTO task : taskDTO) {
            addTask(task);
        }

        return getTasks();
    }

    public TaskDTO getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task not found with id: " + id));
    }

    // actualizeaza toate informatiile unui task
    @Transactional
    public TaskDTO updateTaskById(Long id, TaskCreateDTO taskCreateDTO) {
        Task task = getTaskEntityOrThrow(id);

        task.setTaskName(taskCreateDTO.getTaskName());
        task.setStatusType(taskMapper.resolveStatusType(taskCreateDTO.getStatusTypeId()));
        task.setUser(taskMapper.resolveUser(taskCreateDTO.getUserId()));
        task.setDueDate(taskCreateDTO.getDueDate());

        //se va seta lastUpdatedBy din token

        Task saved = taskRepository.save(task);

        log.info("Updated Task: {} ", saved);

        return taskMapper.toDTO(saved);
    }

    @Transactional
    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    // filtreaza task-urile in functie de criteriile primite
    public List<TaskDTO> filterTasks(TaskFilterDTO filter) {

        List<TaskDTO> result = new ArrayList<>();

        for (Task task : taskRepository.findAll()) {
            if (checkStatus(task, filter)
                    && checkTaskName(task, filter)
                    && checkUser(task, filter)
                    && checkDueDateTime(task, filter)) {
                result.add(taskMapper.toDTO(task));
            }
        }

        return result;
    }

    // verifica daca statusul task-ului corespunde filtrului
    private boolean checkStatus(Task task, TaskFilterDTO filter) {

        if (filter.getStatusName() == null) {
            return true;
        }

        return task.getStatusType() != null
                && filter.getStatusName().equalsIgnoreCase(task.getStatusType().getStatusName());
    }

    // verifica daca numele task-ului contine textul cautat
    private boolean checkTaskName(Task task, TaskFilterDTO filter) {

        if (filter.getTaskName() == null) {
            return true;
        }

        return task.getTaskName() != null
                && task.getTaskName().toLowerCase().contains(filter.getTaskName().toLowerCase());
    }

    // verifica daca userul asignat corespunde filtrului
    private boolean checkUser(Task task, TaskFilterDTO filter) {

        if (filter.getUsername() == null) {
            return true;
        }

        return task.getUser() != null
                && filter.getUsername().equalsIgnoreCase(task.getUser().getUsername());
    }

    // verifica daca data limita corespunde filtrului
    private boolean checkDueDateTime(Task task, TaskFilterDTO filter) {

        if (filter.getDueDateTime() == null) {
            return true;
        }

        return task.getDueDate() != null
                && task.getDueDate().isEqual(filter.getDueDateTime().toLocalDate());
    }

    // actualizeaza doar statusul unui task
    @Transactional
    public TaskDTO updateStatus(Long id, String statusTypeId) {

        Task task = getTaskEntityOrThrow(id);
        StatusType statusType= getStatusTypeEntityOrThrow(statusTypeId);

        task.setStatusType(statusType);
        //se va seta lastUpdatedBy din token

        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
    }

    // actualizeaza doar userul asignat unui task
    @Transactional
    public TaskDTO updateUser(Long id, Long userId) {

        Task task = getTaskEntityOrThrow(id);
        User user = getUserEntityOrThrow(userId);

        task.setUser(user);
        //se va seta lastUpdatedBy din token

        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
    }


    // actualizeaza doar data limita a unui task
    @Transactional
    public TaskDTO updateDueDateTime(Long id, LocalDateTime dueDateTime) {
        Task task = getTaskEntityOrThrow(id);

        task.setDueDate(dueDateTime != null ? dueDateTime.toLocalDate() : null);
        //se va seta lastUpdatedBy din token

        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
    }

    // numara task-urile in functie de status sau returneaza numarul tuturor task-urile
    public long countTasks(String statusTypeId) {
        if (statusTypeId == null) {
            return taskRepository.count();
        }
        return taskRepository.countByStatusType_StatusTypeId(statusTypeId);
    }

    // returneaza task-urile care au depasit data limita
    public List<TaskDTO> getOverdueTasks() {
        return taskRepository.findByDueDateBefore(LocalDate.now())
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    // returneaza task-urile care sunt in interval
    public List<TaskDTO> getTasksDueBetween(LocalDate start, LocalDate end) {
        return taskRepository.findTasksDueBetween(start, end)
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    private Task getTaskEntityOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task not found with id: " + id));
    }

    private User getUserEntityOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + id));
    }

    private StatusType getStatusTypeEntityOrThrow(String id) {
        return statusTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Status type not found with id: " + id));
    }
}
