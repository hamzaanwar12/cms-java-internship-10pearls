package com.recky.demo.service;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.UserRepository;
import com.recky.demo.exception.UserNotFoundException;
import com.recky.demo.model.User;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Create or Update a user
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Find a user by ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Find a user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Find a user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Delete a user
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // Get a paginated list of users
    public Page<User> getUsersPage(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    // Find a user by ID or throw an exception if not found
    public User getUserByIdOrThrow(String userId) {
        logger.info("Searching for user with ID: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User with ID " + userId + " not found");
                });
    }

    //some after end
    public Page<User> getUsersByUserIdPaginated(String userId, Pageable pageable) {
        return userRepository.findById(userId, pageable);
    }

    public Page<User> getAllPageUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
   
    
}
