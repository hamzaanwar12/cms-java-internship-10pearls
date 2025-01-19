package com.recky.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recky.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Find a user by username
    Optional<User> findByUsername(String username);

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Find a user by ID (provided by JpaRepository by default)
    Optional<User> findById(String id);
}
