package org.example.tasks.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.model.User;
import org.example.tasks.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO created = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{userId}")
    public UserDTO updateUser(@PathVariable Long userId,
                              @Valid @RequestBody UserCreateDTO userCreateDTO) {
        return userService.updateUser(userId, userCreateDTO);
    }


    @PutMapping("/{userId}/upsert")
    public ResponseEntity<UserDTO> updateOrCreateUser(@PathVariable Long userId,
                                                      @Valid @RequestBody UserCreateDTO userCreateDTO) {

        boolean exists = userService.existsById(userId);
        UserDTO result = userService.updateOrCreateUser(userId, userCreateDTO);

        HttpStatus status = exists ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(result);
    }

    @GetMapping("/internal/{isInternal}")
    public List<UserDTO> getUserInternal(@PathVariable Boolean isInternal) {
        return  userService.getUsersByIsInternal(isInternal);
    }

    @GetMapping("/whit-tasks")
    public List<UserDTO> getUserWithTasks() {
        return userService.getUsersWhitTasks();
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{oldUserId}/reassign-to/{newUserId}")
    public ResponseEntity<Void> reassignToUser(@PathVariable Long oldUserId, @PathVariable Long newUserId) {
        userService.reassignAndDeleteUser(oldUserId, newUserId);
        return ResponseEntity.noContent().build();
    }
}
