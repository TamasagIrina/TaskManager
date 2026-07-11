package org.example.tasks.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.request.UserCreateDTO;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.mapper.UserMapper;
import org.example.tasks.model.User;
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

    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        log.info("Creating user {}", userCreateDTO);
        User user = userMapper.toEntity(userCreateDTO);

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserDTO updateUser(Long userId, UserCreateDTO userCreateDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a fost gasit niciun user cu id-ul: " + userId));

        existingUser.setUsername(userCreateDTO.getUsername());
        existingUser.setBirthDate(userCreateDTO.getBirthDate());
        existingUser.setIsInternal(userCreateDTO.getIsInternal());


        User saved = userRepository.save(existingUser);
        return userMapper.toDTO(saved);
    }

    // upsert: daca userId exista -> update, altfel -> creeaza unul nou
    public UserDTO updateOrCreateUser(Long userId, UserCreateDTO userCreateDTO) {
        if (userRepository.existsById(userId)) {
            return updateUser(userId, userCreateDTO);
        }
        return createUser(userCreateDTO);
    }


    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nu a fost gasit niciun user cu id-ul: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
