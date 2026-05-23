package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.response.OccupationResponseDTO;
import com.insurance.thinux.insytespringboot.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/occupations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OccupationController {

    private final OccupationService occupationService;

    @GetMapping
    public ResponseEntity<List<OccupationResponseDTO>> getAllOccupations() {
        return ResponseEntity.ok(occupationService.getAllOccupations());
    }
}