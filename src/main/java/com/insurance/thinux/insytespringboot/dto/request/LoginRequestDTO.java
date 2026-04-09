package com.insurance.thinux.insytespringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private boolean rememberMe;
}
