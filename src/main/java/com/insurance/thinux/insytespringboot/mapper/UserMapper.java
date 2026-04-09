package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.model.Role;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: THINUX
 * @created: 21-Feb-26 - 05:22 PM
 */

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());

        if (user.getSupervisor() != null) {
            dto.setSupervisorId(user.getSupervisor().getId());
            dto.setSupervisorUsername(user.getSupervisor().getUsername());
        }

        if (user.getRoles() != null) {
            Set<String> roles = user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            dto.setRoles(roles);
        }

        dto.setStatus(user.getStatus());

        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }
}
