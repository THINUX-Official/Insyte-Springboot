package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.request.UserRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.enums.UserStatus;
import com.insurance.thinux.insytespringboot.service.UserService;
import com.insurance.thinux.insytespringboot.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: THINUX
 * @created: 19-Feb-26 - 09:16 PM
 */

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<UserResponseDTO>>> getAllUsers(@RequestParam(defaultValue = "ACTIVE") UserStatus status) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Users fetched successfully", userService.getAllUsers(status)));
    }

    @PostMapping
    public ResponseEntity<StandardResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(new StandardResponse<>(201, "User created successfully", userService.createUser(dto)));
    }

    @GetMapping("/by-username")
    public ResponseEntity<StandardResponse<UserResponseDTO>> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.ok(new StandardResponse<>(200, "User fetched successfully", userService.getUserByUsername(username)));
    }

    @PutMapping("/by-username")
    public ResponseEntity<StandardResponse<UserResponseDTO>> updateUser(@RequestParam String username, @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(new StandardResponse<>(200, "User updated successfully", userService.updateUser(username, dto)));
    }

    @PutMapping("/delete/by-username")
    public ResponseEntity<StandardResponse<UserResponseDTO>> deleteUser(@RequestParam String username) {
        return ResponseEntity.ok(new StandardResponse<>(200, "User deleted successfully", userService.softDeleteUser(username)));
    }
}
