package org.example.tasks.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.response.TaskDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.model.Project;
import org.example.tasks.model.StatusType;
import org.example.tasks.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private final JavaMailSender mailSender;


    @Async("mailExecutor")
    public void sendSimpleEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendNewTaskEmail(String to, Task taskDTO)  {

        Context context = new Context();
        context.setVariable("taskName",taskDTO.getTaskName());
        context.setVariable("dueDate", taskDTO.getDueDate());
        context.setVariable("status", statusName(taskDTO.getStatusType()));
        String htmlBody = templateEngine.process("newTaskEmail", context);

        this.sendSimpleEmail(to, "New Task", htmlBody);


    }

    public void sendDueDateReminderEmail(String to, Task task) {
        Context context = new Context();
        context.setVariable("taskName", task.getTaskName());
        context.setVariable("dueDate", task.getDueDate());
        context.setVariable("status", statusName(task.getStatusType()));

        long daysLeft = task.getDueDate() != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate())
                : 0;
        context.setVariable("daysLeft", daysLeft);

        String htmlBody = templateEngine.process("dueDateReminderEmail", context);
        this.sendSimpleEmail(to, "Your task due date is coming up", htmlBody);
    }

    public void sendNewProjectEmail(String to, Project project) {
        String htmlBody = templateEngine.process("newProjectEmail", projectContext(project));
        this.sendSimpleEmail(to, "You have been added to a new project", htmlBody);
    }

    public void sendProjectUpdatedEmail(String to, Project project) {
        String htmlBody = templateEngine.process("projectUpdatedEmail", projectContext(project));
        this.sendSimpleEmail(to, "A project you are part of was updated", htmlBody);
    }

    public void sendAddedToProjectEmail(String to, Project project) {
        String htmlBody = templateEngine.process("addedToProjectEmail", projectContext(project));
        this.sendSimpleEmail(to, "You have been added to a project", htmlBody);
    }

    private Context projectContext(Project project) {
        Context context = new Context();
        context.setVariable("projectName", project.getProjectName());
        context.setVariable("projectDescription", project.getProjectDescription());
        context.setVariable("status", statusName(project.getStatusType()));
        return context;
    }

    private String statusName(StatusType statusType) {
        return statusType != null ? statusType.getStatusName() : "—";
    }
    public void sendConfirmationEmail(String to, String userName) {
        String hashEmail = Base64.getEncoder().encodeToString(to.getBytes());;
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("confirmationLink", "http://localhost:8080/register/confirm?hashEmail="+ hashEmail  );

        String htmlBody = templateEngine.process("confirmEmail", context);
        this.sendSimpleEmail(to, "Confirm your email", htmlBody);
    }


}
