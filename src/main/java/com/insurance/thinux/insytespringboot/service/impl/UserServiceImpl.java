package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.request.UserRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.enums.UserStatus;
import com.insurance.thinux.insytespringboot.mapper.UserMapper;
import com.insurance.thinux.insytespringboot.model.Role;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.RoleRepository;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import com.insurance.thinux.insytespringboot.service.HierarchyService;
import com.insurance.thinux.insytespringboot.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: THINUX
 * @created: 19-Feb-26 - 09:31 PM
 */

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final HierarchyService hierarchyService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, HierarchyService hierarchyService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.hierarchyService = hierarchyService;
    }

    @Override
    public List<UserResponseDTO> getAllUsers(UserStatus status) {
        return userRepository.findAllByStatusOrderByIdDesc(status).stream().map(userMapper::toResponseDTO).toList();
    }

    @Override
    public List<UserResponseDTO> getMyTeamUsers() {

        if (hierarchyService.isCurrentUserAdmin()) {
            return userRepository.findAllByStatusOrderByIdDesc(UserStatus.ACTIVE).stream().map(userMapper::toResponseDTO).toList();
        }

        List<Long> visibleUserIds = hierarchyService.getVisibleUserIdsForCurrentUser();

        if (visibleUserIds.isEmpty()) {
            return List.of();
        }

        return userRepository.findByIdInAndStatusOrderByIdDesc(visibleUserIds, UserStatus.ACTIVE).stream().map(userMapper::toResponseDTO).toList();
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email address already exists");
        }

        if (dto.getPhone() != null && userRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());

        if (dto.getSupervisorId() != null) {
            User supervisor = userRepository.findById(dto.getSupervisorId()).orElseThrow(() -> new RuntimeException("Supervisor not found"));
            user.setSupervisor(supervisor);
        }

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = dto.getRoleIds().stream().map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"))).collect(Collectors.toSet());

            user.setRoles(roles);
        }

        user.setStatus(UserStatus.ACTIVE);

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndStatus(username, UserStatus.ACTIVE).orElseThrow(() -> new RuntimeException("Active user not found"));

        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(String username, UserRequestDTO dto) {

        User user = userRepository.findByUsernameAndStatus(username, UserStatus.ACTIVE).orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getSupervisorId() != null) {
            User supervisor = userRepository.findById(dto.getSupervisorId()).orElseThrow(() -> new RuntimeException("Supervisor not found"));
            user.setSupervisor(supervisor);
        } else {
            user.setSupervisor(null);
        }

        if (dto.getRoleIds() != null) {
            Set<Role> roles = dto.getRoleIds().stream().map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"))).collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO softDeleteUser(String username) {
        User user = userRepository.findByUsernameAndStatus(username, UserStatus.ACTIVE).orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.INACTIVE);
        return userMapper.toResponseDTO(userRepository.save(user));
    }
}
