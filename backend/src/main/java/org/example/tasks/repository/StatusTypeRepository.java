package org.example.tasks.repository;

import org.example.tasks.model.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusTypeRepository extends JpaRepository<StatusType, String> {

    //toate status-urile, sortate alfabetic dupa nume
    List<StatusType> findAllByOrderByStatusNameAsc();
}
