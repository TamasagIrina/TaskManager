package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.model.Project;
import org.example.tasks.model.Task;
import org.example.tasks.model.User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;

    // trimitem doar daca userul are email si l-a confirmat
    private boolean canEmail(User user) {
        return user != null
                && user.getEmail() != null
                && Boolean.TRUE.equals(user.getEmailConfirmed());
    }

    public void notifyNewTask(User user, Task task) {
        if (!canEmail(user)) return;
        safeSend(user.getEmail(), () -> emailService.sendNewTaskEmail(user.getEmail(), task));
    }

    public void notifyDueDateReminder(User user, Task task) {
        if (!canEmail(user)) return;
        safeSend(user.getEmail(), () -> emailService.sendDueDateReminderEmail(user.getEmail(), task));
    }

    public void notifyNewProject(User user, Project project) {
        if (!canEmail(user)) return;
        safeSend(user.getEmail(), () -> emailService.sendNewProjectEmail(user.getEmail(), project));
    }

    public void notifyProjectUpdated(User user, Project project) {
        if (!canEmail(user)) return;
        safeSend(user.getEmail(), () -> emailService.sendProjectUpdatedEmail(user.getEmail(), project));
    }

    public void notifyAddedToProject(User user, Project project) {
        if (!canEmail(user)) return;
        safeSend(user.getEmail(), () -> emailService.sendAddedToProjectEmail(user.getEmail(), project));
    }

    // ca sa nu se blocheze
    private void safeSend(String to, Runnable send) {
        try {
            send.run();
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
