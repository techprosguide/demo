package com.example.demo.controller;

import com.example.demo.api.ApiResponse;
import com.example.demo.dao.UserDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("API: Create user");

        UserDto created = userService.createUser(userDto);

        return ResponseEntity.ok(new ApiResponse<>(true, "User created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        logger.info("API: Get user by ID: {}", id);

        UserDto user = userService.getUserById(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "User fetched successfully", user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(Pageable pageable) {
        logger.info("API: Get all users, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDto> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users fetched successfully", users));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {
        logger.info("API: Update user with ID: {}", id);

        UserDto updatedUser = userService.updateUser(id, userDto);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        logger.info("API: Delete user with ID: {}", id);

        userService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User deleted successfully", null));
    }
}
