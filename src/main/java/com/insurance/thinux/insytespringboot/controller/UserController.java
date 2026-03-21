package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.request.UserRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.service.UserService;
import com.insurance.thinux.insytespringboot.util.StandardResponse;
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
    public ResponseEntity<StandardResponse<List<UserResponseDTO>>> getAllUsers() {

        List<UserResponseDTO> users = userService.getAllUsers();

        StandardResponse<List<UserResponseDTO>> response = new StandardResponse<>(200, "Get All Users Successfully", users);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StandardResponse<UserResponseDTO>> createUser(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(new StandardResponse<>(201, "User Created Successfully", userService.createUser(dto)));
    }

    @GetMapping("/id")
    public ResponseEntity<StandardResponse<UserResponseDTO>> getUserById(@RequestParam long id) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Get User Successfully", userService.getUserById(id)));
    }

    @GetMapping("/by-username")
    public ResponseEntity<StandardResponse<UserResponseDTO>> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Get User Successfully", userService.getUserByUsername(username)));
    }
}
