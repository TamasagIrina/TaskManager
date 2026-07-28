package org.example.tasks.security;

import lombok.AllArgsConstructor;
import org.example.tasks.model.Permission;
import org.example.tasks.model.User;
import org.example.tasks.repository.UserRepository;
import org.example.tasks.service.TaskService;
import org.example.tasks.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.stream;

@Component("permissionChecker")
@AllArgsConstructor
public class PermissionChecker {

    private final UserRepository userRepository;

    public boolean checkPermission(String resource, String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal()!=null) {

            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            Long id = principal.getId();

            User user= userRepository.findById(id).orElse(null);

            if (user != null) {
                List<Permission> permissions= user.getRole().getPermissions().stream().toList();
                return permissions.stream()
                        .anyMatch(p -> p.getResource().equals(resource) && p.getPermissionAction().equals(action));
            }
        }

        return false;


    }

}
