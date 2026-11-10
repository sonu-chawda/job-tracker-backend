package com.jobtracker.controller;

import com.jobtracker.dto.Dtos.*;
import com.jobtracker.model.ApplicationStatus;
import com.jobtracker.service.ApplicationService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/applications") @RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService service;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest r, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(auth.getName(), r));
    }

    @GetMapping
    public PageResponse<ApplicationResponse> list(@RequestParam(required = false) ApplicationStatus status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
                                                  @RequestParam(defaultValue = "desc") String dir,
                                                  Authentication auth) {
        return service.list(auth.getName(), status, page, size, sortBy, dir);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(Authentication auth) { return service.stats(auth.getName()); }

    @GetMapping("/{id}")
    public ApplicationResponse get(@PathVariable Long id, Authentication auth) { return service.get(id, auth.getName()); }

    @PutMapping("/{id}")
    public ApplicationResponse update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest r, Authentication auth) {
        return service.update(id, auth.getName(), r);
    }

    @PatchMapping("/{id}/status")
    public ApplicationResponse status(@PathVariable Long id, @Valid @RequestBody StatusRequest r, Authentication auth) {
        return service.changeStatus(id, auth.getName(), r.status());
    }

    @GetMapping("/{id}/history")
    public List<HistoryResponse> history(@PathVariable Long id, Authentication auth) { return service.history(id, auth.getName()); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
