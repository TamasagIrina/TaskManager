package org.example.tasks.service;

import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.request.TaskCreateDTO;
import org.example.tasks.dto.request.TaskFilterDTO;
import org.example.tasks.dto.response.TaskDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class TaskService {
    private List<TaskDTO> tasks= new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    public List<TaskDTO> getTasks() {
        log.info("Getting Tasks: ");

        return tasks;
    }


    public List<TaskDTO> addTask(TaskCreateDTO taskCreateDTO) {
        TaskDTO newTask = buildTask(taskCreateDTO);

        tasks.add(newTask);

        log.info("Added Task: {} " , newTask);

        return tasks;
    }


    public List<TaskDTO> addTasks(List<TaskCreateDTO> taskDTO) {
       for (TaskCreateDTO task : taskDTO) {
           addTask(task);
       }

        return tasks;
    }

    public TaskDTO getTaskById(Long id) {

        for (TaskDTO task : tasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }

        throw new NoSuchElementException("Task not found with id: " + id);
    }

    // actualizeaza toate informatiile unui task
    public TaskDTO updateTaskById(Long id, TaskCreateDTO taskCreateDTO) {

        TaskDTO task= getTaskById(id);
        task.setId(id);
        task.setContent(taskCreateDTO.getContent());
        task.setStatus(taskCreateDTO.getStatus());

        log.info("Updated Task: {} " , task);

        return task;
    }


    public void deleteTaskById(Long id) {

        for (TaskDTO taskDTO : tasks) {
            if (taskDTO.getId().equals(id)) {
                tasks.remove(taskDTO);
                return;
            }
        }

        throw new NoSuchElementException("Task not found with id: " + id);
    }

    // filtreaza task-urile in functie de criteriile primite
    public List<TaskDTO> filterTasks(TaskFilterDTO filter) {

        List<TaskDTO> result = new ArrayList<>();

        for (TaskDTO task : tasks) {
            if (checkStatus(task, filter)
                    && checkContent(task, filter)
                    && checkDueDateTime(task, filter)) {
                result.add(task);
            }
        }


        return result;
    }

    // verifica daca statusul task-ului corespunde filtrului
    private boolean checkStatus(TaskDTO task, TaskFilterDTO filter) {

        if (filter.getStatus() == null) {
            return true;
        }

        return task.getStatus().equalsIgnoreCase(filter.getStatus());
    }

    // verifica daca descrierea task-ului contine textul cautat
    private boolean checkContent(TaskDTO task, TaskFilterDTO filter) {

        if (filter.getContent() == null) {
            return true;
        }

        return task.getContent().toLowerCase().contains(filter.getContent().toLowerCase());
    }

    // verifica daca data limita corespunde filtrului
    private boolean checkDueDateTime(TaskDTO task, TaskFilterDTO filter) {

        if (filter.getDueDateTime() == null) {
            return true;
        }

        return task.getDueDateTime().equals(filter.getDueDateTime());
    }

    // actualizeaza doar statusul unui task
    public TaskDTO updateStatus(Long id, String status) {

        TaskDTO task = getTaskById(id);

        task.setStatus(status);

        return task;
    }

    // actualizeaza doar data limita a unui task
    public TaskDTO updateDueDateTime(Long id, LocalDateTime  dueDateTime) {
        TaskDTO task = getTaskById(id);

        task.setDueDateTime(dueDateTime);

        return task;
    }

    // numara task-urile in functie de status sau returneaza numarul tuturor task-urile
    public long countTasks(String status) {

        long count = 0;

        if (status == null) {
            return tasks.size();
        }

        for (TaskDTO task : tasks) {
            if (task.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }

    // returneaza task-urile care au depasit data limita
    public List<TaskDTO> getOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();

        List<TaskDTO> overdue = new ArrayList<>();

        for (TaskDTO task : tasks) {
            if (task.getDueDateTime() != null && task.getDueDateTime().isBefore(now)) {
                overdue.add(task);
            }
        }


        return overdue;
    }


    private TaskDTO buildTask(TaskCreateDTO taskCreateDTO) {
        return TaskDTO.builder()
                .id(idGenerator.getAndIncrement())
                .content(taskCreateDTO.getContent())
                .status(taskCreateDTO.getStatus())
                .dueDateTime(taskCreateDTO.getDueDateTime())
                .build();
    }
}
