package org.example.tasks.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.tasks.dto.response.UserDTO;
import org.example.tasks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIsInternal(Boolean isInternal);

    User findByEmail(@NotBlank() @Size(max = 255) String email);
}
