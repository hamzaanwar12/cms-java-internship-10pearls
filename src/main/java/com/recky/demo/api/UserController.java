package com.recky.demo.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recky.demo.dto.UserDTO;
import com.recky.demo.model.User;
import com.recky.demo.service.UserService;
import com.recky.demo.util.ApiResponse;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create a user
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            UserDTO userDTO = toUserDTO(savedUser);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.CREATED.value(), "success",
                    "User created successfully", userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while creating user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Update a user
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            User existingUser = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Only update fields that are provided
            if (user.getUsername() != null)
                existingUser.setUsername(user.getUsername());
            if (user.getEmail() != null)
                existingUser.setEmail(user.getEmail());
            if (user.getRole() != null)
                existingUser.setRole(user.getRole());
            if (user.getPassword() != null)
                existingUser.setPassword(user.getPassword());

            User updatedUser = userService.saveUser(existingUser);
            UserDTO userDTO = toUserDTO(updatedUser);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "User updated successfully", userDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while updating user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get a user by ID
    @GetMapping("/get-userById/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = toUserDTO(user);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "User retrieved successfully", userDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "User not found",
                    null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while retrieving user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get a user by username
    @GetMapping("/get-userByUsername/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = toUserDTO(user);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "User retrieved successfully", userDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "User not found",
                    null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while retrieving user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get a user by email
    @GetMapping("/get-userByEmail/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = toUserDTO(user);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "User retrieved successfully", userDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "User not found",
                    null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while retrieving user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get users by role
    @GetMapping("/get-usersByRole/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable String role) {
        try {
            List<UserDTO> users = userService.getAllUsers()
                    .stream()
                    .filter(user -> user.getRole().toString().equalsIgnoreCase(role))
                    .map(this::toUserDTO)
                    .collect(Collectors.toList());

            if (users.isEmpty()) {
                ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error",
                        "No users found with this role", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "Users retrieved successfully", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while retrieving users", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get all users
    @GetMapping("/get-users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers()
                    .stream()
                    .map(this::toUserDTO)
                    .collect(Collectors.toList());

            if (users.isEmpty()) {
                ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error",
                        "No users found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "All users retrieved successfully", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while retrieving users", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "success",
                    "User deleted successfully", null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RuntimeException e) {
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "User not found",
                    null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while deleting user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // paginated version of get all users
    // Get all users with pagination
    @GetMapping("/get-pageUsers")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Page<User> userPage = userService.getUsersPage(page, size);
            List<UserDTO> users = userPage.getContent()
                    .stream()
                    .map(this::toUserDTO)
                    .collect(Collectors.toList());

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "success",
                    "Users retrieved successfully",
                    users,
                    userPage.getTotalPages(),
                    userPage.getNumber(),
                    (int) userPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while retrieving users", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get users by role with pagination
    @GetMapping("/get-pageUsersByRole/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(
            @PathVariable String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Page<User> userPage = userService.getUsersPage(page, size);
            List<UserDTO> users = userPage.getContent()
                    .stream()
                    .filter(user -> user.getRole().toString().equalsIgnoreCase(role))
                    .map(this::toUserDTO)
                    .collect(Collectors.toList());

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "success",
                    "Users retrieved successfully",
                    users,
                    userPage.getTotalPages(),
                    userPage.getNumber(),
                    (int) userPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<UserDTO>> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while retrieving users", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper method to map User to UserDTO
    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(), // Assuming User has a 'getName()' method
                user.getRole().toString(),
                user.getStatus() != null ? user.getStatus().toString() : "ACTIVE", // Assuming status is an Enum or
                                                                                   // String
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeactivatedAt(),
                user.getDeactivatedBy(),
                user.getContacts() != null ? user.getContacts().size() : 0);
    }

}
