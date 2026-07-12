package org.example.tasks.repository;

import org.example.tasks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIsInternal(Boolean isInternal);

    @Query("SELECT DISTINCT u FROM User u JOIN u.tasks t")
    List<User> findUsersWithTasks();
}
