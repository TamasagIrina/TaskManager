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

        UserDTO result = userService.updateOrCreateUser(userId, userCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
