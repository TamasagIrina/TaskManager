package org.example.tasks.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.tasks.dto.request.StatusTypeCreateDTO;
import org.example.tasks.dto.response.StatusTypeDTO;
import org.example.tasks.service.StatusTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statuses")
@RequiredArgsConstructor
@CrossOrigin
public class StatusTypeController {

    private final StatusTypeService statusTypeService;


    @GetMapping
    @PreAuthorize("@permissionChecker.checkPermission('statusess', 'get all')")
    public List<StatusTypeDTO> getAllStatuses() {
        return statusTypeService.getAllStatuses();
    }

    @PostMapping
    @PreAuthorize("@permissionChecker.checkPermission('statusess', 'create')")
    public StatusTypeDTO createStatus(@RequestBody @Valid StatusTypeCreateDTO statusTypeCreateDTO) {
        return statusTypeService.createStatuses(statusTypeCreateDTO);
    }


    @GetMapping("/{statusTypeId}")
    @PreAuthorize("@permissionChecker.checkPermission('statusess', 'get all')")
    public StatusTypeDTO getStatusById(@PathVariable String statusTypeId) {
        return statusTypeService.getStatusById(statusTypeId);
    }

    @PutMapping("/{statusTypeId}")
    @PreAuthorize("@permissionChecker.checkPermission('statusess', 'update')")
    public StatusTypeDTO updateStatusType(@PathVariable String statusTypeId,
                                          @Valid @RequestBody StatusTypeCreateDTO statusTypeCreateDTO) {
        return statusTypeService.updateStatusType(statusTypeId, statusTypeCreateDTO);
    }

    @PutMapping("/{statusTypeId}/upsert")
    @PreAuthorize("@permissionChecker.checkPermission('statusess', 'update')")
    public ResponseEntity<StatusTypeDTO> upsertStatusType(@PathVariable String statusTypeId,
                                                          @Valid @RequestBody StatusTypeCreateDTO statusTypeCreateDTO) {

        boolean exists = statusTypeService.existsById(statusTypeId);
        StatusTypeDTO result = statusTypeService.updateOrCreateStatusType(statusTypeId, statusTypeCreateDTO);

        HttpStatus status = exists ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(result);
    }

    @GetMapping("/asc")
    @PreAuthorize("@permissionChecker.checkPermission('statusess', 'get all')")
    public List<StatusTypeDTO> getStatusesByStatusNameAsc() {
        return statusTypeService.getStatusesByStatusNameAsc();
    }

    @DeleteMapping("/{statusTypeId}")
    @PreAuthorize("@permissionChecker.checkPermission('statusess', 'delete')")
    public ResponseEntity<Void> deleteStatusType(@PathVariable String statusTypeId) {
        statusTypeService.deleteStatusType(statusTypeId);
        return ResponseEntity.noContent().build();
    }
}
