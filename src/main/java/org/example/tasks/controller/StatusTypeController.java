package org.example.tasks.controller;

import jakarta.validation.Valid;
import org.example.tasks.dto.request.StatusTypeCreateDTO;
import org.example.tasks.dto.response.StatusTypeDTO;
import org.example.tasks.service.StatusTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statuses")
public class StatusTypeController {
    private StatusTypeService statusTypeService;

    public StatusTypeController(StatusTypeService statusTypeService) {
        this.statusTypeService = statusTypeService;
    }

    @GetMapping
    public List<StatusTypeDTO> getAllStatuses() {
        return statusTypeService.getAllStatuses();
    }

    @PostMapping
    public StatusTypeDTO createStatus(@RequestBody @Valid StatusTypeCreateDTO statusTypeCreateDTO) {
        return statusTypeService.createStatuses(statusTypeCreateDTO);
    }


    @GetMapping("/{statusTypeId}")
    public StatusTypeDTO getStatusById(@PathVariable String statusTypeId) {
        return statusTypeService.getStatusById(statusTypeId);
    }

    @PutMapping("/{statusTypeId}")
    public StatusTypeDTO updateStatusType(@PathVariable String statusTypeId,
                                          @Valid @RequestBody StatusTypeCreateDTO statusTypeCreateDTO) {
        return statusTypeService.updateStatusType(statusTypeId, statusTypeCreateDTO);
    }

    @PutMapping("/{statusTypeId}/upsert")
    public StatusTypeDTO upsertStatusType(@PathVariable String statusTypeId,
                                          @Valid @RequestBody StatusTypeCreateDTO statusTypeCreateDTO) {
        return statusTypeService.updateOrCreateStatusType(statusTypeId, statusTypeCreateDTO);
    }

    @DeleteMapping("/{statusTypeId}")
    public ResponseEntity<Void> deleteStatusType(@PathVariable String statusTypeId) {
        statusTypeService.deleteStatusType(statusTypeId);
        return ResponseEntity.noContent().build();
    }
}
