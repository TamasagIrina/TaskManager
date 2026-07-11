package org.example.tasks.mapper;

import org.example.tasks.dto.request.StatusTypeCreateDTO;
import org.example.tasks.dto.response.StatusTypeDTO;
import org.example.tasks.model.StatusType;
import org.springframework.stereotype.Component;

@Component
public class StatusTypeMapper {
    // Entitate -> DTO de raspuns (ce trimitem catre client)
    public StatusTypeDTO toDTO(StatusType statusType) {
        return StatusTypeDTO.builder()
                .statusTypeId(statusType.getStatusTypeId())
                .statusName(statusType.getStatusName())
                .createdBy(statusType.getCreatedBy())
                .creationDate(statusType.getCreationDate())

                .build();
    }

    // DTO de request -> Entitate (ce primim de la client)
    public StatusType toEntity(StatusTypeCreateDTO requestDTO) {
        return StatusType.builder()
                .statusName(requestDTO.getStatusName())
                .build();
    }
}
