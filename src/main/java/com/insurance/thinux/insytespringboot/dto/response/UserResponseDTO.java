package com.insurance.thinux.insytespringboot.dto.response;

import com.insurance.thinux.insytespringboot.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author: THINUX
 * @created: 21-Feb-26 - 05:01 PM
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String nickname;

    private Long supervisorId;
    private UserStatus status;
    private String supervisorUsername;

    private Set<String> roles;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
