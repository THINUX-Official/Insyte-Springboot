package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.request.LeadRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.LeadResponseDTO;
import com.insurance.thinux.insytespringboot.service.LeadService;
import com.insurance.thinux.insytespringboot.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<LeadResponseDTO>>> getAllLeads() {
        return ResponseEntity.ok(new StandardResponse<>(200, "All leads", leadService.getAllLeads()));
    }

    @PostMapping
    public ResponseEntity<StandardResponse<LeadResponseDTO>> createLead(@Valid @RequestBody LeadRequestDTO dto) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Lead created", leadService.createLead(dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<LeadResponseDTO>> getLead(@PathVariable Long id) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Lead fetched", leadService.getLeadById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<LeadResponseDTO>> updateLead(@PathVariable Long id, @RequestBody LeadRequestDTO dto) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Lead updated", leadService.updateLead(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(new StandardResponse<>(200, "Lead deleted", null));
    }
}
