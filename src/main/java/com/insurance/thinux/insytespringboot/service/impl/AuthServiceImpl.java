package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.request.LoginRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.LoginResponseDTO;
import com.insurance.thinux.insytespringboot.enums.UserStatus;
import com.insurance.thinux.insytespringboot.model.Role;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import com.insurance.thinux.insytespringboot.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsernameAndStatus(request.getUsername(), UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Invalid username or inactive user"));

        System.out.println("User found: " + user.getUsername());
        System.out.println("Role count: " + user.getRoles().size());
        user.getRoles().forEach(role -> System.out.println("Role: " + role.getName()));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Invalid password");
        }

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new LoginResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getNickname(),
                user.getStatus().name(),
                roles,
                "Login successful"
        );
    }
}
