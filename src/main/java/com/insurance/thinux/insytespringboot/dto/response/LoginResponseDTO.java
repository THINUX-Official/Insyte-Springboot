package com.insurance.thinux.insytespringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String nickname;
    private String status;
    private Set<String> roles;

    private String token;
    private String tokenType;

    private String message;
}