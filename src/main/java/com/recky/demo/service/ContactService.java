package com.recky.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.ContactRepository;
import com.recky.demo.dto.ContactDTO;
import com.recky.demo.model.Contact;

import jakarta.transaction.Transactional;

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
    public List<Contact> getContactsByUserId(String userId) {
        return contactRepository.findAllByUserId(userId);
    }

    // Get a specific contact by user ID and contact ID
    public Optional<Contact> getContactByUserIdAndId(String userId, Long contactId) {
        return contactRepository.findByUserIdAndId(userId, contactId);
    }

    public Optional<Contact> getContactByUserIdAndContactId(String userId, Long contactId) {
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

    public void deleteContact(Long contactId, String userId) {
        logger.debug("Deleting contact for userId: {} and contactId: {}", userId, contactId);

        if (userId == null || contactId == null) {
            logger.error("Invalid userId or contactId: userId={}, contactId={}", userId, contactId);
            throw new IllegalArgumentException("userId and contactId must not be null");
        }

        Optional<Contact> contactOpt = contactRepository.findByUserIdAndId(userId, contactId);

        if (contactOpt.isEmpty()) {
            logger.warn("No contact found for userId: {} and contactId: {}", userId, contactId);
            throw new RuntimeException("Contact not found for the specified userId and contactId");
        }
        logger.info("contact found for userId: {} and contactId: {}", userId, contactId);

        Contact contact = contactOpt.get();
        contactRepository.delete(contact);

        logger.info("Deleted contact with userId: {} and contactId: {}", userId, contactId);
        activityLogService.logActivity(userId, "DELETE",
                "Deleted contact with ID: " + contactId + " belonging to user: " + userId);
    }

    // Get all contacts for a user, filtering based on optional criteria (email or
    // phone)
    public List<Contact> getContactsByUserIdWithFilters(String userId, Optional<String> email, Optional<String> phone) {
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
    public List<Contact> getContactsByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return contactRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    // Get a paginated list of contacts for a user
    public Page<Contact> getContactsByUserIdPaginated(String userId, Pageable pageable) {
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
            String userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return contactRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate, pageable);
    }

    public boolean existsByPhoneAndUserId(String phone, String userId) {
        return contactRepository.existsByPhoneAndUserId(phone, userId);
    }

    public Optional<Contact> getContactByPhoneAndUserId(String phone, String userId) {
        return contactRepository.findByPhoneAndUserId(phone, userId);
    }

    public boolean existsByIdAndUserId(Long id, String userId) {
        return contactRepository.existsByIdAndUserId(id, userId);
    }

    public boolean existsById(Long id) {
        return contactRepository.existsById(id);
    }

    // Some new chnages
    // Get all contacts paginated for ADMIN users
    // public Page<Contact> getAllContactsPaginated(Pageable pageable) {
    // return contactRepository.findAll(pageable);
    // }

    public Page<ContactDTO> getAllContactsPaginated(Pageable pageable) {
        // Fetch paginated contacts and map them to ContactDTO
        return contactRepository.findAll(pageable)
                .map(contact -> new ContactDTO(
                        contact.getId(),
                        contact.getUser() != null ? contact.getUser().getId() : null, // Map userId
                        contact.getName(),
                        contact.getPhone(),
                        contact.getEmail(),
                        contact.getAddress(),
                        contact.getCreatedAt(),
                        contact.getUpdatedAt()));
    }

}