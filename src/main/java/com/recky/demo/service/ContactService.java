package com.recky.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.ContactRepository;
import com.recky.demo.model.Contact;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public boolean existsByPhone(String phone) {
        return contactRepository.existsByPhone(phone);
    }

    public boolean existsByPhoneAndIdNot(String phone, Long id) {
        return contactRepository.existsByPhoneAndIdNot(phone, id);
    }

    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
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
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
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

}