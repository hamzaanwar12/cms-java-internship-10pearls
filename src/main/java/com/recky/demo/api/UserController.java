package com.recky.demo.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.recky.demo.dto.UserStatsDTO;
import com.recky.demo.exception.UserNotFoundException;
import com.recky.demo.model.User;
import com.recky.demo.service.ActivityLogService;
import com.recky.demo.service.UserService;
import com.recky.demo.util.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @Autowired
    private ActivityLogService activityLogService; // Inject ActivityLogService

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
            // if (user.getPassword() != null)
            // existingUser.setPassword(user.getPassword());

            User updatedUser = userService.saveUser(existingUser);
            UserDTO userDTO = toUserDTO(updatedUser);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.OK.value(),
                    "success",
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
                // user.getName(), // Assuming User has a 'getName()' method
                user.getRole().toString(),
                user.getStatus() != null ? user.getStatus().toString() : "ACTIVE", // Assuming status is an Enum or
                                                                                   // String
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeactivatedAt(),
                user.getDeactivatedBy(),
                user.getContacts() != null ? user.getContacts().size() : 0);
    }

    // Some Extra after testing:
    @GetMapping("/{userId}/paginated-users")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getPaginatedUsersByUserId(
            @PathVariable String userId,
            Pageable pageable) {

        try {
            // Fetch users with pagination
            // Page<User> userPage = userService.getUsersByUserIdPaginated(userId,
            // pageable);
            Page<User> userPage = userService.getAllPageUsers(pageable);

            // Log activity
            activityLogService.logActivity(userId, "GET", "Fetched paginated users for userId: " + userId);

            // Map to DTO
            Page<UserDTO> userDTOPage = userPage.map(this::toUserDTO);

            // Build API response
            ApiResponse<Page<UserDTO>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "success",
                    "Users fetched successfully",
                    userDTOPage,
                    userDTOPage.getTotalPages(),
                    userDTOPage.getNumber(),
                    (int) userDTOPage.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Page<UserDTO>> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error",
                    "An error occurred while fetching users",
                    null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update-user/{id}/{performedId}")
    public ResponseEntity<ApiResponse<UserDTO>> NewupdateUser(
            @PathVariable String id,
            @PathVariable String performedId,
            @RequestBody User user) {
        try {
            // Fetch the existing user
            User existingUser = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            User existingPerformerUser = userService.getUserById(performedId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update only allowed fields
            if (user.getEmail() != null) {
                // Check if the email is already used by another user
                Optional<User> conflictingUser = userService.getUserByEmail(user.getEmail());
                if (conflictingUser.isPresent() && !conflictingUser.get().getId().equals(id)) {
                    activityLogService.logActivity(performedId, "UPDATE",
                            "Conflict on " + id + "update : Email " + user.getEmail()
                                    + " is already used by another user");
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), "error",
                                    "Email is already in use by another user", null));
                }
                existingUser.setEmail(user.getEmail());
            }

            if (user.getUsername() != null) {
                // Check if the username is already used by another user
                Optional<User> conflictingUser = userService.getUserByUsername(user.getUsername());
                if (conflictingUser.isPresent() && !conflictingUser.get().getId().equals(id)) {
                    activityLogService.logActivity(performedId, "UPDATE",
                            "Conflict on " + id + "Username update " + user.getUsername()
                                    + " is already used by another user");
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), "error",
                                    "Username is already in use by another user", null));
                }
                existingUser.setUsername(user.getUsername());
            }

            if (user.getStatus() != null) {
                existingUser.setStatus(user.getStatus());
            }

            // Save the updated user
            User updatedUser = userService.saveUser(existingUser);

            // Convert to DTO and prepare the response
            UserDTO userDTO = toUserDTO(updatedUser);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "User updated successfully", userDTO);

            // Log successful update
            activityLogService.logActivity(performedId, "UPDATE", "Successfully updated user with ID: " + id);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Log activity for user not found
            activityLogService.logActivity(id, "UPDATE", "Attempted to update non-existent user with ID: " + id);
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error",
                    "User not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            // Log activity for unexpected errors
            activityLogService.logActivity(id, "UPDATE",
                    "An error occurred while updating user with ID: " + id + ". Error: " + e.getMessage());
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while updating user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete-user/{id}/{performedId}")
    public ResponseEntity<ApiResponse<UserDTO>> deleteUser(
            @PathVariable String id,
            @PathVariable String performedId) {
        try {
            // Check if the user performing the action exists
            User performingUser = userService.getUserById(performedId)
                    .orElseThrow(() -> new RuntimeException("Performing user not found"));

            // Check if the user to be deleted exists
            User userToDelete = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User to delete not found"));

            // Convert the user to DTO before deletion for returning in the response
            UserDTO userDTO = toUserDTO(userToDelete);

            // Perform the deletion
            userService.deleteUser(id);

            // Log successful deletion activity
            activityLogService.logActivity(performedId, "DELETE",
                    "Successfully deleted user with ID: " + id);

            // Return a success response with the deleted user details
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "User deleted successfully", userDTO);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Log activity for user not found or other runtime issues
            activityLogService.logActivity(performedId, "DELETE",
                    "Failed to delete user with ID: " + id + ". Reason: " + e.getMessage());

            // Return a not found response
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error",
                    e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            // Log activity for unexpected errors
            activityLogService.logActivity(performedId, "DELETE",
                    "An error occurred while deleting user with ID: " + id + ". Error: " + e.getMessage());

            // Return an internal server error response
            ApiResponse<UserDTO> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                    "An error occurred while deleting user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/get-stats/{userId}") 
    public ResponseEntity<ApiResponse<UserStatsDTO>> getUserStatistics(@PathVariable String userId) { 
        try { 
            // Log the incoming request details 
            logger.info("Fetching user statistics for userId: {}", userId); 
     
            // Fetch the user 
            User user = userService.getUserByIdOrThrow(userId); 
     
            // Log the user's role 
            logger.info("User role: {}", user.getRole()); 
     
            // Check admin role using the enum 
            if (user.getRole() == null || user.getRole() != User.Role.ADMIN) { 
                logger.warn("Access denied: User {} is not an admin", userId); 
                return ResponseEntity.status(HttpStatus.FORBIDDEN) 
                        .body(new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "error", 
                                "Access denied", null)); 
            } 
     
            // Fetch user statistics 
            UserStatsDTO userStats = userService.getUserStatistics(); 
     
            // Log the activity 
            activityLogService.logActivity(userId, "GET", "Fetched user statistics"); 
     
            // Return the response 
            return ResponseEntity.ok( 
                    new ApiResponse<>( 
                            HttpStatus.OK.value(), 
                            "success", 
                            "User statistics fetched successfully", 
                            userStats)); 
        } catch (Exception e) { 
            // Log the full exception details 
            logger.error("Error fetching user statistics for userId: {}", userId, e); 
     
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) 
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", 
                            "An error occurred while fetching user statistics: " + e.getMessage(), null)); 
        } 
    }
}
