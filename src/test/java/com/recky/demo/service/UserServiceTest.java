package com.recky.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.recky.demo.dao.UserRepository;
import com.recky.demo.exception.UserNotFoundException;
import com.recky.demo.model.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== Initializing Test Setup ===");
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(User.Role.USER);
        System.out.println("Created test user with ID: " + testUser.getId() + 
                          ", Username: " + testUser.getUsername());
    }

    @Test
    void saveUser_Success() {
        System.out.println("\n=== Testing User Save Operation ===");
        System.out.println("Configuring mock repository to return test user on save");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        System.out.println("Attempting to save user: " + testUser.getUsername());
        User savedUser = userService.saveUser(testUser);

        System.out.println("Verifying save operation results...");
        assertNotNull(savedUser, "Saved user should not be null");
        assertEquals(testUser.getUsername(), savedUser.getUsername(), 
                    "Username should match original");
        verify(userRepository).save(any(User.class));
        System.out.println("User saved successfully with username: " + savedUser.getUsername());
    }

    @Test
    void getUserById_Found() {
        System.out.println("\n=== Testing Get User By ID (Found) ===");
        System.out.println("Configuring mock repository to return test user for ID: " + testUser.getId());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        System.out.println("Attempting to find user by ID: " + testUser.getId());
        Optional<User> found = userService.getUserById(1L);

        System.out.println("Verifying found user details...");
        assertTrue(found.isPresent(), "User should be found");
        assertEquals(testUser.getUsername(), found.get().getUsername(), 
                    "Username should match");
        System.out.println("User found successfully with username: " + found.get().getUsername());
    }

    @Test
    void getUserById_NotFound() {
        System.out.println("\n=== Testing Get User By ID (Not Found) ===");
        Long nonExistentId = 1L;
        System.out.println("Configuring mock repository to return empty for ID: " + nonExistentId);
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        System.out.println("Attempting to find non-existent user...");
        Optional<User> found = userService.getUserById(nonExistentId);

        System.out.println("Verifying user was not found...");
        assertFalse(found.isPresent(), "User should not be found");
        System.out.println("Test completed successfully - No user found as expected");
    }

    @Test
    void getUserByUsername_Found() {
        System.out.println("\n=== Testing Get User By Username ===");
        System.out.println("Configuring mock repository to return test user for username: " + 
                          testUser.getUsername());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        System.out.println("Attempting to find user by username...");
        Optional<User> found = userService.getUserByUsername("testuser");

        System.out.println("Verifying found user details...");
        assertTrue(found.isPresent(), "User should be found");
        assertEquals(testUser.getUsername(), found.get().getUsername(), 
                    "Username should match");
        System.out.println("User found successfully with username: " + found.get().getUsername());
    }

    @Test
    void getAllUsers_Success() {
        System.out.println("\n=== Testing Get All Users ===");
        List<User> users = Arrays.asList(testUser);
        System.out.println("Configuring mock repository to return list of " + users.size() + " users");
        when(userRepository.findAll()).thenReturn(users);

        System.out.println("Attempting to retrieve all users...");
        List<User> foundUsers = userService.getAllUsers();

        System.out.println("Verifying retrieved users...");
        assertFalse(foundUsers.isEmpty(), "User list should not be empty");
        assertEquals(1, foundUsers.size(), "Should have found 1 user");
        System.out.println("Successfully retrieved " + foundUsers.size() + " users");
    }

    @Test
    void deleteUser_Success() {
        System.out.println("\n=== Testing Delete User ===");
        Long userId = 1L;
        System.out.println("Configuring mock repository for delete operation of user ID: " + userId);
        doNothing().when(userRepository).deleteById(userId);

        System.out.println("Attempting to delete user...");
        userService.deleteUser(userId);

        System.out.println("Verifying delete operation was called...");
        verify(userRepository).deleteById(userId);
        System.out.println("User deletion operation completed successfully");
    }

    @Test
    void getUsersPage_Success() {
        System.out.println("\n=== Testing Get Users Page ===");
        Page<User> page = new PageImpl<>(Arrays.asList(testUser));
        System.out.println("Configuring mock repository to return paginated result");
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);

        System.out.println("Attempting to retrieve page of users...");
        Page<User> result = userService.getUsersPage(0, 10);

        System.out.println("Verifying pagination results...");
        assertNotNull(result, "Page result should not be null");
        assertEquals(1, result.getContent().size(), "Page should contain 1 user");
        System.out.println("Successfully retrieved page with " + 
                          result.getContent().size() + " users");
    }

    @Test
    void getUserByIdOrThrow_Found() {
        System.out.println("\n=== Testing Get User By ID Or Throw (Found) ===");
        System.out.println("Configuring mock repository to return test user for ID: " + testUser.getId());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        System.out.println("Attempting to find user...");
        User found = userService.getUserByIdOrThrow(1L);

        System.out.println("Verifying found user details...");
        assertNotNull(found, "User should not be null");
        assertEquals(testUser.getUsername(), found.getUsername(), "Username should match");
        System.out.println("User found successfully with username: " + found.getUsername());
    }

    @Test
    void getUserByIdOrThrow_NotFound() {
        System.out.println("\n=== Testing Get User By ID Or Throw (Not Found) ===");
        Long nonExistentId = 1L;
        System.out.println("Configuring mock repository to return empty for ID: " + nonExistentId);
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        System.out.println("Attempting to find non-existent user...");
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByIdOrThrow(nonExistentId);
        }, "Should throw UserNotFoundException");
        
        System.out.println("Verifying exception details...");
        String expectedMessage = "User with ID " + nonExistentId + " not found";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage, "Exception message should match");
        System.out.println("Test completed successfully - Exception thrown as expected");
    }
}