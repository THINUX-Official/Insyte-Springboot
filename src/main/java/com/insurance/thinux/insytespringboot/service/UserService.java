package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.request.UserRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.enums.UserStatus;

import java.util.List;

/**
 * @author: THINUX
 * @created: 19-Feb-26 - 09:29 PM
 */
public interface UserService {

    List<UserResponseDTO> getAllUsers(UserStatus status);

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO getUserByUsername(String username);

    UserResponseDTO updateUser(String username, UserRequestDTO dto);

    UserResponseDTO softDeleteUser(String username);
}
