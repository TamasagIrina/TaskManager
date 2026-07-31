package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.model.Task;
import org.example.tasks.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskReminderService {



    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    // ruleaza zilnic la 08:00, trimite reminder pentru task-urile cu due date in urmatoarele 3 zile
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void sendDueDateReminders() {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(3);

        List<Task> tasks = taskRepository.findTasksDueBetween(today, limit);

        int sent = 0;
        for (Task task : tasks) {
            if (isActive(task)) {
                notificationService.notifyDueDateReminder(task.getUser(), task);
                sent++;
            }
        }

        log.info("Due date reminders: {} tasks in window, {} eligible for notification", tasks.size(), sent);
    }

    // nu trimite reminder pentru task-uri finalizate sau anulate
    private boolean isActive(Task task) {
        if (task.getStatusType() == null || task.getStatusType().getStatusName() == null) {
            return true;
        }
        String status = task.getStatusType().getStatusName();
        return !status.equalsIgnoreCase("Done") && !status.equalsIgnoreCase("Cancelled");
    }
}
