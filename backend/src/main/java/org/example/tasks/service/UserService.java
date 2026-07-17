package org.example.tasks.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.request.AuthRequest;
import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.dto.response.AuthResponse;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.mapper.UserMapper;
import org.example.tasks.model.Task;
import org.example.tasks.model.User;
import org.example.tasks.repository.TaskRepository;
import org.example.tasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;

    public List<UserDTO> getAllUsers() {
        log.info("Users retrieved from database!");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a fost gasit niciun user cu id-ul: " + userId));
    }

    public AuthResponse logUser(AuthRequest authRequest) {
        User user= userRepository.findByEmail(authRequest.getEmail());
        log.info("User logged in: " + user);
        AuthResponse authResponse = new AuthResponse();

        if(user!=null && authRequest.getPassword()!=null && user.getPassword().equals(authRequest.getPassword())) {
          authResponse.setUser(userMapper.toDTO(user));
          authResponse.setMessage("Logged in");
        }else {
            authResponse.setMessage("Password or email doesn't match");
            authResponse.setUser(null);
        }

        return authResponse;
    }


    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        log.info("Creating user {}", userCreateDTO);
        User user = userMapper.toEntity(userCreateDTO);

        //aici se vor seta createdBy si createdByFullName din token

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserCreateDTO userCreateDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a fost gasit niciun user cu id-ul: " + userId));


        existingUser.setUsername(userCreateDTO.getUsername());
        existingUser.setEmail(userCreateDTO.getEmail());
        existingUser.setPassword(userCreateDTO.getPassword());
        existingUser.setBirthDate(userCreateDTO.getBirthDate());

        //se va seta lastUpdatedBy din token

        User saved = userRepository.save(existingUser);
        return userMapper.toDTO(saved);
    }

    // upsert- daca userId exista update, altfel creeaza unul nou
    @Transactional
    public UserDTO updateOrCreateUser(Long userId, UserCreateDTO userCreateDTO) {
        if (userRepository.existsById(userId)) {
            return updateUser(userId, userCreateDTO);
        }

        return createUser(userCreateDTO);
    }

    public List<UserDTO> getUsersByIsInternal(Boolean isInternal) {
        return userRepository.findByIsInternal(isInternal)
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }


    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nu a fost gasit niciun user cu id-ul: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void reassignAndDeleteUser(Long oldUserId, Long newUserId) {

        if (oldUserId.equals(newUserId)) {
            throw new IllegalArgumentException("User-ul vechi si cel nou nu pot fi acelasi");
        }

        User oldUser = userRepository.findById(oldUserId)
                .orElseThrow(() -> new EntityNotFoundException("User-ul de sters nu a fost gasit"));

        User newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new EntityNotFoundException("User-ul nou nu a fost gasit"));

        List<Task> tasks = taskRepository.findByUser_UserId(oldUserId);
        tasks.forEach(t -> t.setUser(newUser));
        taskRepository.saveAll(tasks);

        userRepository.delete(oldUser);
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
