package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.request.LoginRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.LoginResponseDTO;

public interface AuthService {
    public LoginResponseDTO login(LoginRequestDTO request);
}
