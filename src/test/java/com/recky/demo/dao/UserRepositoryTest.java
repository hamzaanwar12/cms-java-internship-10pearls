package com.recky.demo.dao;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.recky.demo.model.User;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID().toString()); // Manually set UUID
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setName("Test User");
    }

    @Test
    void whenFindById_thenReturnUser() {
        System.out.println("\n=== Testing findById with valid ID ===");
        System.out.println("Persisting test user to database...");
        User persistedUser = entityManager.persist(testUser);
        entityManager.flush();
        System.out.println("User persisted with ID: " + persistedUser.getId());

        System.out.println("Attempting to find user by ID: " + persistedUser.getId());
        Optional<User> found = userRepository.findById(persistedUser.getId());

        System.out.println("Verifying user was found and matches expected data...");
        assertTrue(found.isPresent(), "User should be found in database");
        assertEquals(testUser.getUsername(), found.get().getUsername(),
                "Username should match original");
        System.out.println("Test completed successfully - User found by ID");
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        System.out.println("\n=== Testing findByUsername with valid username ===");
        System.out.println("Persisting test user to database...");
        entityManager.persist(testUser);
        entityManager.flush();

        System.out.println("Searching for user with username: " + testUser.getUsername());
        Optional<User> found = userRepository.findByUsername(testUser.getUsername());

        System.out.println("Verifying user data matches...");
        assertTrue(found.isPresent(), "User should be found by username");
        assertEquals(testUser.getUsername(), found.get().getUsername(),
                "Username should match");
        assertEquals(testUser.getEmail(), found.get().getEmail(),
                "Email should match");
        System.out.println("Test completed successfully - User found by username");
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        System.out.println("\n=== Testing findByEmail with valid email ===");
        System.out.println("Persisting test user to database...");
        entityManager.persist(testUser);
        entityManager.flush();

        System.out.println("Searching for user with email: " + testUser.getEmail());
        Optional<User> found = userRepository.findByEmail(testUser.getEmail());

        System.out.println("Verifying user data matches...");
        assertTrue(found.isPresent(), "User should be found by email");
        assertEquals(testUser.getEmail(), found.get().getEmail(),
                "Email should match");
        assertEquals(testUser.getUsername(), found.get().getUsername(),
                "Username should match");
        System.out.println("Test completed successfully - User found by email");
    }

    @Test
    void whenFindByUsername_thenReturnEmpty() {
        System.out.println("\n=== Testing findByUsername with non-existent username ===");
        String nonexistentUsername = "nonexistent";
        System.out.println("Attempting to find user with username: " + nonexistentUsername);

        Optional<User> found = userRepository.findByUsername(nonexistentUsername);

        System.out.println("Verifying no user was found...");
        assertFalse(found.isPresent(), "No user should be found with non-existent username");
        System.out.println("Test completed successfully - No user found as expected");
    }

    @Test
    void whenFindByEmail_thenReturnEmpty() {
        System.out.println("\n=== Testing findByEmail with non-existent email ===");
        String nonexistentEmail = "nonexistent@example.com";
        System.out.println("Attempting to find user with email: " + nonexistentEmail);

        Optional<User> found = userRepository.findByEmail(nonexistentEmail);

        System.out.println("Verifying no user was found...");
        assertFalse(found.isPresent(), "No user should be found with non-existent email");
        System.out.println("Test completed successfully - No user found as expected");
    }

    @Test
    void whenSaveUser_thenReturnSavedUser() {
        System.out.println("\n=== Testing user save operation ===");
        System.out.println("Attempting to save test user...");

        User savedUser = userRepository.save(testUser);

        System.out.println("Verifying saved user data...");
        assertNotNull(savedUser.getId(), "Saved user should have an ID");
        assertEquals(testUser.getUsername(), savedUser.getUsername(),
                "Username should match original");
        assertEquals(testUser.getEmail(), savedUser.getEmail(),
                "Email should match original");
        System.out.println("Test completed successfully - User saved with ID: " +
                savedUser.getId());
    }

    @Test
    void whenFindById_thenReturnEmpty() {
        System.out.println("\n=== Testing findById with non-existent ID ===");
        String nonexistentId = "nonexistent-id"; // Now using String ID
        System.out.println("Attempting to find user with ID: " + nonexistentId);

        Optional<User> found = userRepository.findById(nonexistentId);

        System.out.println("Verifying no user was found...");
        assertFalse(found.isPresent(), "No user should be found with non-existent ID");
        System.out.println("Test completed successfully - No user found as expected");
    }
}
