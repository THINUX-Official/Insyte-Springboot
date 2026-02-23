package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.mapper.UserMapper;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import com.insurance.thinux.insytespringboot.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: THINUX
 * @created: 19-Feb-26 - 09:31 PM
 */

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponseDTO).toList();
    }
}
