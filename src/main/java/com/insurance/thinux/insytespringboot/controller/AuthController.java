package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.request.LoginRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.LoginResponseDTO;
import com.insurance.thinux.insytespringboot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
