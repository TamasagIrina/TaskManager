package org.example.tasks.repository;

import org.example.tasks.model.Role;
import org.springframework.data.repository.CrudRepository;


public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRoleName(String roleName);
}
