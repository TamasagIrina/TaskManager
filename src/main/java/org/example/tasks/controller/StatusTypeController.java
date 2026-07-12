package org.example.tasks.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.StatusTypeCreateDTO;
import org.example.tasks.dto.response.StatusTypeDTO;
import org.example.tasks.service.StatusTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statuses")
@RequiredArgsConstructor
public class StatusTypeController {

    private final StatusTypeService statusTypeService;


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
    public ResponseEntity<StatusTypeDTO> upsertStatusType(@PathVariable String statusTypeId,
                                                          @Valid @RequestBody StatusTypeCreateDTO statusTypeCreateDTO) {

        boolean exists = statusTypeService.existsById(statusTypeId);
        StatusTypeDTO result = statusTypeService.updateOrCreateStatusType(statusTypeId, statusTypeCreateDTO);

        HttpStatus status = exists ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(result);
    }

    @GetMapping("/asc")
    public List<StatusTypeDTO> getStatusesByStatusNameAsc() {
        return statusTypeService.getStatusesByStatusNameAsc();
    }

    @DeleteMapping("/{statusTypeId}")
    public ResponseEntity<Void> deleteStatusType(@PathVariable String statusTypeId) {
        statusTypeService.deleteStatusType(statusTypeId);
        return ResponseEntity.noContent().build();
    }
}
