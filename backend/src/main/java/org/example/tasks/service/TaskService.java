package org.example.tasks.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.request.TaskCreateDTO;
import org.example.tasks.dto.request.TaskFilterDTO;
import org.example.tasks.dto.response.PageResponse;
import org.example.tasks.dto.response.TaskDTO;
import org.example.tasks.dto.response.UserTaskStatsDTO;
import org.example.tasks.mapper.TaskMapper;
import org.example.tasks.model.Project;
import org.example.tasks.model.StatusType;
import org.example.tasks.model.Task;
import org.example.tasks.model.User;
import org.example.tasks.repository.ProjectRepository;
import org.example.tasks.repository.StatusTypeRepository;
import org.example.tasks.repository.TaskRepository;
import org.example.tasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final StatusTypeRepository statusTypeRepository;
    private final TaskMapper taskMapper;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

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

        if (newTask.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Task must have an assigned user");
        }

        Long userId = newTask.getUser().getUserId();

        Project project = projectRepository.findById(taskCreateDTO.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Project not found with id: " + taskCreateDTO.getProjectId()));

        ensureUserIsProjectMember(userId, project);

        newTask.setCreatedByFullName(currentUserService.getCurrentUser().getUsername());


        taskRepository.save(newTask);
        log.info("Added Task: {} ", newTask);

        notificationService.notifyNewTask(newTask.getUser(), newTask);

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

    public List<TaskDTO> getTaskByUserId(Long userId) {
        List<Task> tasks = taskRepository.findByUser_UserId(userId);
        return tasks.stream()
                .map(taskMapper::toDTO)
                .toList();

    }

    // actualizeaza toate informatiile unui task
    @Transactional
    public TaskDTO updateTaskById(Long id, TaskCreateDTO taskCreateDTO) {
        Task task = getTaskEntityOrThrow(id);

        Long previousUserId = task.getUser() != null ? task.getUser().getUserId() : null;

        User newUser = taskMapper.resolveUser(taskCreateDTO.getUserId());
        if (newUser != null) {
            ensureUserIsProjectMember(newUser.getUserId(), task.getProject());
        }

        task.setTaskName(taskCreateDTO.getTaskName());
        task.setStatusType(taskMapper.resolveStatusType(taskCreateDTO.getStatusTypeId()));
        task.setUser(newUser);
        task.setDueDate(taskCreateDTO.getDueDate());

        Task saved = taskRepository.save(task);

        boolean assigneeChanged = newUser != null && !newUser.getUserId().equals(previousUserId);
        if (assigneeChanged) {
            notificationService.notifyNewTask(task.getUser(), task);
        }

        log.info("Updated Task: {} ", saved);

        return taskMapper.toDTO(saved);
    }




    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Task not found with id: " + id);
        }
        log.info("Deleted Task: {} ", taskRepository.findById(id));
        taskRepository.deleteById(id);
    }

    // filtreaza task-urile user-ului in functie de criteriile primite (paginat + sortat)
    public PageResponse<TaskDTO> filterTasksUser(Long userId, TaskFilterDTO filter, int page, int size,
                                                 String sortBy, String sortDir) {

        List<TaskDTO> result = new ArrayList<>();

        for (Task task : taskRepository.findByUser_UserId(userId)) {
            if (checkStatus(task, filter)
                    && checkTaskName(task, filter)
                    && checkProject(task, filter)
                    && checkDueDateTime(task, filter)) {
                result.add(taskMapper.toDTO(task));
            }
        }

        return paginate(sortTasks(result, sortBy, sortDir), page, size);
    }

    // filtreaza task-urile in functie de criteriile primite (paginat + sortat)
    public PageResponse<TaskDTO> filterTasks(TaskFilterDTO filter, int page, int size,
                                             String sortBy, String sortDir) {

        List<TaskDTO> result = new ArrayList<>();

        for (Task task : taskRepository.findAll()) {
            if (checkStatus(task, filter)
                    && checkTaskName(task, filter)
                    && checkUser(task, filter)
                    && checkProject(task, filter)
                    && checkDueDateTime(task, filter)) {
                result.add(taskMapper.toDTO(task));
            }
        }

        return paginate(sortTasks(result, sortBy, sortDir), page, size);
    }

    // sortare dinamica dupa id / username / taskName, ascendent sau descendent
    private List<TaskDTO> sortTasks(List<TaskDTO> tasks, String sortBy, String sortDir) {
        String field = sortBy == null ? "id" : sortBy.toLowerCase();

        Comparator<TaskDTO> comparator = switch (field) {
            case "username" -> Comparator.comparing(TaskDTO::getUsername,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "taskname" -> Comparator.comparing(TaskDTO::getTaskName,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "duedate" -> Comparator.comparing(TaskDTO::getDueDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(TaskDTO::getTaskId,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };

        // tie-breaker stabil dupa id, ca paginile sa fie deterministe
        comparator = comparator.thenComparing(TaskDTO::getTaskId,
                Comparator.nullsLast(Comparator.naturalOrder()));

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        tasks.sort(comparator);
        return tasks;
    }

    // taie lista intr-o pagina si construieste metadatele
    private PageResponse<TaskDTO> paginate(List<TaskDTO> all, int page, int size) {
        int pageSize = size <= 0 ? 8 : size;
        int currentPage = Math.max(page, 0);

        int total = all.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        int from = Math.min(currentPage * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<TaskDTO> content = new ArrayList<>(all.subList(from, to));
        return new PageResponse<>(content, currentPage, pageSize, total, totalPages);
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

    // verifica daca numele proiectului contine textul cautat
    private boolean checkProject(Task task, TaskFilterDTO filter) {

        if (filter.getProjectName() == null) {
            return true;
        }

        return task.getProject() != null
                && task.getProject().getProjectName() != null
                && task.getProject().getProjectName().toLowerCase()
                        .contains(filter.getProjectName().toLowerCase());
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

        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
    }

    // actualizeaza doar userul asignat unui task
    @Transactional
    public TaskDTO updateUser(Long id, Long userId) {

        Task task = getTaskEntityOrThrow(id);
        User user = getUserEntityOrThrow(userId);

        Long previousUserId = task.getUser() != null ? task.getUser().getUserId() : null;
        boolean assigneeChanged = !user.getUserId().equals(previousUserId);
        if (assigneeChanged) {
            ensureUserIsProjectMember(userId, task.getProject());
        }

        task.setUser(user);
        Task saved = taskRepository.save(task);

        if (assigneeChanged) {
            notificationService.notifyNewTask(task.getUser(), task);
        }

        return taskMapper.toDTO(saved);
    }


    // actualizeaza doar data limita a unui task
    @Transactional
    public TaskDTO updateDueDateTime(Long id, LocalDate dueDateTime) {
        Task task = getTaskEntityOrThrow(id);

        task.setDueDate(dueDateTime != null ? dueDateTime : null);

        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
    }

    // numara task-urile in functie de status sau returneaza numarul tuturor task-urile
    public long countTasks(String statusName) {
        if (statusName == null) {
            return taskRepository.count();
        }
        return taskRepository.countByStatusType_StatusName(statusName);
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

    // numara task-urile unui user, in functie de status sau toate
    public long countTasksByUserId(Long userId, String statusName) {
        if (statusName == null) {
            return taskRepository.countByUser_UserId(userId);
        }
        return taskRepository.countByUser_UserIdAndStatusType_StatusName(userId, statusName);
    }

    // returneaza task-urile unui user care au depasit data limita
    public List<TaskDTO> getOverdueTasksByUserId(Long userId) {
        return taskRepository.findByUser_UserIdAndDueDateBefore(userId, LocalDate.now())
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    // returneaza task-urile unui user care sunt in interval
    public List<TaskDTO> getTasksDueBetweenForUser(Long userId, LocalDate start, LocalDate end) {
        return taskRepository.findTasksDueBetweenForUser(userId, start, end)
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public List<UserTaskStatsDTO> getTaskStatsByUser() {
        return taskRepository.getTaskStatsByUser();
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

    // verifica ca userul asignat este membru al proiectului
    private void ensureUserIsProjectMember(Long userId, Project project) {
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Task must belong to a project");
        }

        boolean isMember = project.getMembers() != null && project.getMembers().stream()
                .anyMatch(u -> u.getUserId().equals(userId));

        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User " + userId + " is not a member of project " + project.getProjectId());
        }
    }


}
