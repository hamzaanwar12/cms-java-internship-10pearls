package com.recky.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recky.demo.dao.ContactRepository;
import com.recky.demo.model.Contact;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    // Create or Update a contact
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    // Find a contact by ID
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    // Find all contacts for a user
    public List<Contact> getContactsByUserId(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    // Delete a contact
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}
