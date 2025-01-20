package com.recky.demo.api;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recky.demo.dto.UserDTO;
import com.recky.demo.model.User;
import com.recky.demo.service.UserService;
import com.recky.demo.util.ApiResponse;

class UserControllerTest {

        @Mock
        private UserService userService;

        @InjectMocks
        private UserController userController;

        private User testUser;

        @BeforeEach
        void setUp() {
                System.out.println("\n=== Initializing Test Setup ===");
                MockitoAnnotations.openMocks(this);

                // Initialize test user
                testUser = new User();
                testUser.setId(UUID.randomUUID().toString()); // Manually set UUID
                testUser.setUsername("testuser");
                testUser.setEmail("test@example.com");
                // testUser.setPassword("password");
                // testUser.setName("Test User");
                System.out.println("Test setup completed");
        }

        @Test
        void testCreateUser_Success() {
                // Arrange
                System.out.println("\n=== Testing Create User Success ===");
                User inputUser = new User();
                inputUser.setUsername("testUser");
                inputUser.setEmail("test@example.com");

                when(userService.saveUser(inputUser)).thenReturn(testUser);

                // Act
                ResponseEntity<ApiResponse<UserDTO>> response = userController.createUser(inputUser);

                // Assert
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("User created successfully", response.getBody().getMessage());
                verify(userService, times(1)).saveUser(inputUser);
        }

        @Test
        void testUpdateUser_Success() {
                // Arrange
                System.out.println("\n=== Testing Update User Success ===");
                String userId = "some-random-id";
                User updateUser = new User();
                updateUser.setUsername("updatedUser");
                updateUser.setEmail("updated@example.com");

                when(userService.getUserById(userId)).thenReturn(Optional.of(testUser));
                when(userService.saveUser(testUser)).thenReturn(testUser);

                // Act
                ResponseEntity<ApiResponse<UserDTO>> response = userController.updateUser(userId, updateUser);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("User updated successfully", response.getBody().getMessage());
        }

        @Test
        void testUpdateUser_UserNotFound() {
                // Arrange
                System.out.println("\n=== Testing Update User Not Found ===");
                String userId = "some-random-id";
                User updateUser = new User();

                when(userService.getUserById(userId)).thenReturn(Optional.empty());

                // Act
                ResponseEntity<ApiResponse<UserDTO>> response = userController.updateUser(userId, updateUser);

                // Assert
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                assertEquals("error", response.getBody().getStatus());
        }

        @Test
        void testGetUserById_UserFound() {
                // Arrange
                System.out.println("\n=== Testing Get User By ID (Found) ===");
                String userId = "some-random-id";

                when(userService.getUserById(userId)).thenReturn(Optional.of(testUser));

                // Act
                ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserById(userId);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("User retrieved successfully", response.getBody().getMessage());
        }

        @Test
        void testGetUserByUsername_Success() {
                // Arrange
                System.out.println("\n=== Testing Get User By Username Success ===");
                String username = "testUser";

                when(userService.getUserByUsername(username)).thenReturn(Optional.of(testUser));

                // Act
                ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserByUsername(username);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("User retrieved successfully", response.getBody().getMessage());
        }

        @Test
        void testGetUserByEmail_Success() {
                // Arrange
                System.out.println("\n=== Testing Get User By Email Success ===");
                String email = "test@example.com";

                when(userService.getUserByEmail(email)).thenReturn(Optional.of(testUser));

                // Act
                ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserByEmail(email);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("User retrieved successfully", response.getBody().getMessage());
        }

        @Test
        void testGetUsersByRole_Success() {
                // Arrange
                System.out.println("\n=== Testing Get Users By Role Success ===");
                String role = "USER";
                List<User> users = Arrays.asList(testUser);

                when(userService.getAllUsers()).thenReturn(users);

                // Act
                ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.getUsersByRole(role);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("Users retrieved successfully", response.getBody().getMessage());
                assertEquals(1, response.getBody().getData().size());
        }

        @Test
        void testGetAllUsers_Success() {
                // Arrange
                System.out.println("\n=== Testing Get All Users Success ===");
                List<User> users = Arrays.asList(testUser);

                when(userService.getAllUsers()).thenReturn(users);

                // Act
                ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.getAllUsers();

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("All users retrieved successfully", response.getBody().getMessage());
                assertEquals(1, response.getBody().getData().size());
        }

        @Test
        void testDeleteUser_Success() {
                // Arrange
                System.out.println("\n=== Testing Delete User Success ===");
                String userId = "some-random-id";

                doNothing().when(userService).deleteUser(userId);

                // Act
                ResponseEntity<ApiResponse<Void>> response = userController.deleteUser(userId);

                // Assert
                assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("User deleted successfully", response.getBody().getMessage());
                verify(userService, times(1)).deleteUser(userId);
        }

        @Test
        void testGetPageUsers_Success() {
                // Arrange
                System.out.println("\n=== Testing Get Page Users Success ===");
                int page = 0;
                int size = 10;
                List<User> users = Arrays.asList(testUser);
                Page<User> userPage = new PageImpl<>(users, PageRequest.of(page, size), users.size());

                when(userService.getUsersPage(page, size)).thenReturn(userPage);

                // Act
                ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.getAllUsers(page, size);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("Users retrieved successfully", response.getBody().getMessage());
                assertEquals(1, response.getBody().getData().size());
        }

        @Test
        void testGetPageUsersByRole_Success() {
                // Arrange
                System.out.println("\n=== Testing Get Page Users By Role Success ===");
                String role = "USER";
                int page = 0;
                int size = 10;
                List<User> users = Arrays.asList(testUser);
                Page<User> userPage = new PageImpl<>(users, PageRequest.of(page, size), users.size());

                when(userService.getUsersPage(page, size)).thenReturn(userPage);

                // Act
                ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.getUsersByRole(role, page, size);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("success", response.getBody().getStatus());
                assertEquals("Users retrieved successfully", response.getBody().getMessage());
                assertEquals(1, response.getBody().getData().size());
        }
}