package com.recky.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.recky.demo.api.ContactController;
import com.recky.demo.dao.ContactRepository;
import com.recky.demo.model.Contact;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ActivityLogService activityLogService; // Inject ActivityLogService

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    public boolean existsByPhone(String phone) {
        return contactRepository.existsByPhone(phone);
    }

    public boolean existsByPhoneAndIdNot(String phone, Long id) {
        return contactRepository.existsByPhoneAndIdNot(phone, id);
    }

    @Transactional
    public Contact saveContact(Contact contact) {
        try {
            return contactRepository.save(contact);
        } catch (Exception e) {
            logger.error("Error saving contact: ", e);
            throw new RuntimeException("Failed to save contact", e);
        }
    }

    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }

    // Get a contact by ID
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    // Get all contacts for a specific user
    public List<Contact> getContactsByUserId(Long userId) {
        return contactRepository.findAllByUserId(userId);
    }

    // Get a specific contact by user ID and contact ID
    public Optional<Contact> getContactByUserIdAndId(Long userId, Long contactId) {
        return contactRepository.findByUserIdAndId(userId, contactId);
    }

    public Optional<Contact> getContactByUserIdAndContactId(Long userId, Long contactId) {
        return contactRepository.findByUserIdAndContactId(userId, contactId);
    }

    // Get a contact by phone
    // public Optional<Contact> getContactByPhone(String phone) {
    // return contactRepository.findByPhone(phone);
    // }

    // Get a contact by email
    // public Optional<Contact> getContactByEmail(String email) {
    // return contactRepository.findByEmail(email);
    // }

    // Get all contacts
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    // Delete a contact
    @Transactional
    public void deleteContact(Long contactId, Long userId) {
        // Perform the deletion logic first
        contactRepository.deleteById(contactId);

        logger.info("Deleted contact with User ID: " + userId + " and Contact ID: " + contactId + "\n");
        logger.info("Deleted contact with User ID: " + userId + " and Contact ID: " + contactId + "\n");
        logger.info("Deleted contact with User ID: " + userId + " and Contact ID: " + contactId + "\n");
        logger.info("Deleted contact with User ID: " + userId + " and Contact ID: " + contactId + "\n");

        // Log the activity
        activityLogService.logActivity(userId, "DELETE", userId, "Deleted contact with ID: " + contactId);
    }

    // Get all contacts for a user, filtering based on optional criteria (email or
    // phone)
    public List<Contact> getContactsByUserIdWithFilters(Long userId, Optional<String> email, Optional<String> phone) {
        if (email.isPresent() && phone.isPresent()) {
            return contactRepository.findByUserIdAndEmailAndPhone(userId, email.get(), phone.get());
        } else if (email.isPresent()) {
            return contactRepository.findByUserIdAndEmail(userId, email.get());
        } else if (phone.isPresent()) {
            return contactRepository.findByUserIdAndPhone(userId, phone.get());
        }
        return getContactsByUserId(userId);
    }

    // Get all contacts created within a specific date range
    public List<Contact> getContactsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return contactRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    // Get a paginated list of contacts for a user
    public Page<Contact> getContactsByUserIdPaginated(Long userId, Pageable pageable) {
        return contactRepository.findByUserId(userId, pageable);
    }

    public List<Contact> getContactsByPhone(String phone) {
        return contactRepository.findByPhone(phone);
    }

    public List<Contact> getContactsByEmail(String email) {
        return contactRepository.findByEmail(email);
    }

    // Get paginated contacts created within a specific date range for a user
    public Page<Contact> getContactsByDateRangePaginated(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return contactRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate, pageable);
    }

    public boolean existsByPhoneAndUserId(String phone, Long userId) {
        return contactRepository.existsByPhoneAndUserId(phone, userId);
    }

    public boolean existsByIdAndUserId(Long id, Long userId) {
        return contactRepository.existsByIdAndUserId(id, userId);
    }

    public boolean existsById(Long id) {
        return contactRepository.existsById(id);
    }

}