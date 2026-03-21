package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.request.UserRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;

import java.util.List;

/**
 * @author: THINUX
 * @created: 19-Feb-26 - 09:29 PM
 */
public interface UserService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByUsername(String username);

    UserRequestDTO updateUser(Long id, UserRequestDTO dto);

    void deleteUser(Long id);
}
