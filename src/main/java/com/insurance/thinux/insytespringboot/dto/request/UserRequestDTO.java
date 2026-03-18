package com.insurance.thinux.insytespringboot.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

    @Email
    private String email;

    private String phone;
    private String nickname;
    private Long supervisorId;
    private Set<Long> roleIds;
}
