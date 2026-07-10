package org.example.tasks.controller;

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
    public StatusTypeDTO createStatus(@RequestBody StatusTypeDTO statusTypeDTO) {
        return statusTypeService.createStatuses(statusTypeDTO);
    }
}
