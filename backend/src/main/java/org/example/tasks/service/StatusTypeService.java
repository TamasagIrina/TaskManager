package org.example.tasks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasks.dto.request.StatusTypeCreateDTO;
import org.example.tasks.dto.response.StatusTypeDTO;
import org.example.tasks.mapper.StatusTypeMapper;
import org.example.tasks.model.StatusType;
import org.example.tasks.repository.StatusTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusTypeService {
    private final StatusTypeRepository statusTypeRepository;

    private final StatusTypeMapper statusTypeMapper;

    public List<StatusTypeDTO> getAllStatuses() {
        log.info("Statuses retrieved from database!");
        return  statusTypeRepository.findAll()
                .stream()
                .map(statusTypeMapper::toDTO)
                .toList();
    }

    public StatusTypeDTO getStatusById(String statusTypeId) {
        return statusTypeRepository.findById(statusTypeId)
                .map(statusTypeMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a am gasit niciun status cu id-ul: " + statusTypeId));
    }

   
    public StatusTypeDTO createStatuses(StatusTypeCreateDTO statusTypeCreateDTO) {
        log.info("Creating status type {}", statusTypeCreateDTO);
        StatusType status = statusTypeMapper.toEntity(statusTypeCreateDTO);

        //aici se vor seta createdBy si createdByFullName din token

        StatusType savedStatus = statusTypeRepository.save(status);

        return statusTypeMapper.toDTO(savedStatus);

    }

    @Transactional
    public StatusTypeDTO updateStatusType(String statusTypeId, StatusTypeCreateDTO statusTypeCreateDTO) {
        StatusType existing = statusTypeRepository.findById(statusTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nu a am gasit niciun status cu id-ul: " + statusTypeId));

        existing.setStatusName(statusTypeCreateDTO.getStatusName());
        //se va seta lastUpdatedBy din token

        StatusType saved = statusTypeRepository.save(existing);
        return statusTypeMapper.toDTO(saved);
    }

    // upsert- daca userId exista update, altfel creeaza unul nou
    @Transactional
    public StatusTypeDTO updateOrCreateStatusType(String statusTypeId, StatusTypeCreateDTO statusTypeCreateDTO) {
        if (statusTypeRepository.existsById(statusTypeId)) {
            return updateStatusType(statusTypeId, statusTypeCreateDTO);
        }
        return createStatuses(statusTypeCreateDTO);
    }

    public List<StatusTypeDTO> getStatusesByStatusNameAsc() {
        return statusTypeRepository.findAllByOrderByStatusNameAsc()
                .stream()
                .map(statusTypeMapper::toDTO)
                .toList();
    }

    @Transactional
    public void deleteStatusType(String statusTypeId) {
        if (!statusTypeRepository.existsById(statusTypeId)) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nu a am gasit niciun status cu id-ul: " + statusTypeId);
        }
        statusTypeRepository.deleteById(statusTypeId);
    }


    public boolean existsById(String statusTypeId) {
        return statusTypeRepository.existsById(statusTypeId);
    }
}
