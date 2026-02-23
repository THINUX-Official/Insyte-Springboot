package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.service.UserService;
import com.insurance.thinux.insytespringboot.util.StandardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        StandardResponse<List<UserResponseDTO>> response = new StandardResponse<>(200, "OK", users);

        return ResponseEntity.ok(response);
    }
}
