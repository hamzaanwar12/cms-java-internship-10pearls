package com.recky.demo.api;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recky.demo.config.TestConfig;
import com.recky.demo.model.User;
import com.recky.demo.service.UserService;



@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(User.Role.USER);
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        ResultActions response = mockMvc.perform(post("/api/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.username").value(testUser.getUsername()));
    }

    @Test
    void updateUser_Success() throws Exception {
        System.out.println("\n=== Testing Update User Success Scenario ===");
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        System.out.println("Performing PUT request to /api/users/update/1");
        ResultActions response = mockMvc.perform(put("/api/users/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.username").value(testUser.getUsername()));
        System.out.println("Update user test completed successfully");
    }

    @Test
    void getUserById_Success() throws Exception {
        System.out.println("\n=== Testing Get User By ID Success Scenario ===");
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        System.out.println("Performing GET request to /api/users/get-userById/1");
        ResultActions response = mockMvc.perform(get("/api/users/get-userById/1")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.username").value(testUser.getUsername()));
        System.out.println("Get user by ID test completed successfully");
    }

    @Test
    void getUserByUsername_Success() throws Exception {
        System.out.println("\n=== Testing Get User By Username Success Scenario ===");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        System.out.println("Performing GET request to /api/users/get-userByUsername/testuser");
        ResultActions response = mockMvc.perform(get("/api/users/get-userByUsername/testuser")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.username").value(testUser.getUsername()));
        System.out.println("Get user by username test completed successfully");
    }

    @Test
    void getUserByEmail_Success() throws Exception {
        System.out.println("\n=== Testing Get User By Email Success Scenario ===");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        System.out.println("Performing GET request to /api/users/get-userByEmail/test@example.com");
        ResultActions response = mockMvc.perform(get("/api/users/get-userByEmail/test@example.com")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.username").value(testUser.getUsername()));
        System.out.println("Get user by email test completed successfully");
    }

    @Test
    void getUsersByRole_Success() throws Exception {
        System.out.println("\n=== Testing Get Users By Role Success Scenario ===");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        System.out.println("Performing GET request to /api/users/get-usersByRole/USER");
        ResultActions response = mockMvc.perform(get("/api/users/get-usersByRole/USER")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data[0].username").value(testUser.getUsername()));
        System.out.println("Get users by role test completed successfully");
    }

    @Test
    void getAllUsers_Success() throws Exception {
        System.out.println("\n=== Testing Get All Users Success Scenario ===");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        System.out.println("Performing GET request to /api/users");
        ResultActions response = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("All users retrieved successfully"))
                .andExpect(jsonPath("$.data[0].username").value(testUser.getUsername()));
        System.out.println("Get all users test completed successfully");
    }

    @Test
    void deleteUser_Success() throws Exception {
        System.out.println("\n=== Testing Delete User Success Scenario ===");
        doNothing().when(userService).deleteUser(1L);

        System.out.println("Performing DELETE request to /api/users/1");
        ResultActions response = mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isNoContent());
        verify(userService).deleteUser(1L);
        System.out.println("Delete user test completed successfully");
    }

    @Test
    void getPageUsers_Success() throws Exception {
        System.out.println("\n=== Testing Get Page Users Success Scenario ===");
        Page<User> page = new PageImpl<>(Arrays.asList(testUser));
        when(userService.getUsersPage(0, 10)).thenReturn(page);

        System.out.println("Performing GET request to /api/users/get-pageUsers");
        ResultActions response = mockMvc.perform(get("/api/users/get-pageUsers")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data[0].username").value(testUser.getUsername()));
        System.out.println("Get page users test completed successfully");
    }

    @Test
    void getPageUsersByRole_Success() throws Exception {
        System.out.println("\n=== Testing Get Page Users By Role Success Scenario ===");
        Page<User> page = new PageImpl<>(Arrays.asList(testUser));
        when(userService.getUsersPage(0, 10)).thenReturn(page);

        System.out.println("Performing GET request to /api/users/get-pageUsersByRole/USER");
        ResultActions response = mockMvc.perform(get("/api/users/get-pageUsersByRole/USER")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data[0].username").value(testUser.getUsername()));
        System.out.println("Get page users by role test completed successfully");
    }

    // Error scenario tests
    @Test
    void getUserById_NotFound() throws Exception {
        System.out.println("\n=== Testing Get User By ID Not Found Scenario ===");
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        System.out.println("Performing GET request to /api/users/get-userById/999");
        ResultActions response = mockMvc.perform(get("/api/users/get-userById/999")
                .contentType(MediaType.APPLICATION_JSON));

        System.out.println("Verifying response...");
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
        System.out.println("Get user by ID not found test completed successfully");
    }

    @Test
    void updateUser_NotFound() throws Exception {
        System.out.println("\n=== Testing Update User Not Found Scenario ===");
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        System.out.println("Performing PUT request to /api/users/update/999");
        ResultActions response = mockMvc.perform(put("/api/users/update/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)));

        System.out.println("Verifying response...");
        response.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An error occurred while updating user"));
        System.out.println("Update user not found test completed successfully");
    }
}