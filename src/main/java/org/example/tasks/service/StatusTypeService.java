package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.response.StatusTypeDTO;
import org.example.tasks.mapper.StatusTypeMapper;
import org.example.tasks.model.StatusType;
import org.example.tasks.repository.StatusTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusTypeService {
    private final StatusTypeRepository repository;
    private final StatusTypeMapper mapper;
    private final StatusTypeMapper statusTypeMapper;

    public List<StatusTypeDTO> getAllStatuses() {
        log.info("Statuses retrieved from database!");
        return  repository.findAll()
                .stream()
                .map(statusTypeMapper::toDTO)
                .toList();
    }

    @Transactional
    public StatusTypeDTO createStatuses(StatusTypeDTO statusTypeDTO) {
        log.info("Creating status type {}", statusTypeDTO);
        StatusType status = mapper.toEntity(statusTypeDTO);
        StatusType savedStatus = repository.save(status);

        return mapper.toDTO(savedStatus);

    }

}
