package com.recky.demo.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recky.demo.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Find all contacts by userId
    List<Contact> findAllByUserId(String userId);

    // Find a specific contact by userId and contactId
    Optional<Contact> findByUserIdAndId(String userId, Long id);

    List<Contact> findByPhone(String phone);

    List<Contact> findByEmail(String email);

    // Find a contact by phone
    // Optional<Contact> findByPhone(String phone);

    // Find a contact by email
    // Optional<Contact> findByEmail(String email);

    List<Contact> findByUserIdAndEmailAndPhone(String userId, String email, String phone);

    List<Contact> findByUserIdAndEmail(String userID, String email);

    List<Contact> findByUserIdAndPhone(String userID, String phone);

    // Find contacts by created_at date range
    List<Contact> findByUserIdAndCreatedAtBetween(String userID, LocalDateTime startDate, LocalDateTime endDate);

    // Paginated query for contacts by userId
    Page<Contact> findByUserId(String userID, Pageable pageable);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    // Paginated query for contacts by userId and created_at date range
    Page<Contact> findByUserIdAndCreatedAtBetween(
            String userID,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    boolean existsByPhoneAndUserId(String phone, String userID);

    // Custom query to check if a contact exists with the given id and user id
    boolean existsByIdAndUserId(Long id, String userID);


    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.id = :contactId")
    Optional<Contact> findByUserIdAndContactId(@Param("userId") String userID, @Param("contactId") Long contactId);

}