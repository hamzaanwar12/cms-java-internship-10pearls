package com.recky.demo.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recky.demo.model.Contact;
import com.recky.demo.service.ContactService;
import com.recky.demo.util.ApiResponse;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    /**
     * Create or update a contact.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Contact>> createContact(@RequestBody Contact contact) {
        try {
            // Check if a contact with the same phone number already exists
            if (contactService.existsByPhone(contact.getPhone())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), "error", "Contact with this phone number already exists", null));
            }
            // Save the new contact
            Contact savedContact = contactService.saveContact(contact);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "success", "Contact created successfully", savedContact));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", "An error occurred while creating the contact", null));
        }
    }


    @PutMapping("/update/{id}")
public ResponseEntity<ApiResponse<Contact>> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
    try {
        // Check if the contact to update exists
        Optional<Contact> existingContact = contactService.findById(id);
        if (!existingContact.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "Contact not found", null));
        }
        // Check if the new phone number is already in use by another contact
        if (contactService.existsByPhoneAndIdNot(contact.getPhone(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), "error", "Another contact with this phone number already exists", null));
        }
        // Update the contact
        contact.setId(id);
        Contact updatedContact = contactService.saveContact(contact);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contact updated successfully", updatedContact));
    } catch (Exception e) {
        // Handle unexpected errors
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error", "An error occurred while updating the contact", null));
    }
}



    /**
     * Get a specific contact by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Optional<Contact>>> getContactById(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        if (contact.isPresent()) {
            return ResponseEntity
                    .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contact fetched successfully", contact));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "Contact not found", null));
    }

    /**
     * Get all contacts for a user, with optional email and phone filters.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByUserId(
            @PathVariable Long userId,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> phone) {

        List<Contact> contacts = contactService.getContactsByUserIdWithFilters(userId, email, phone);
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contacts fetched successfully", contacts));
    }

    /**
     * Get all contacts created within a specific date range for a user.
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByDateRange(
            @PathVariable Long userId,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {

        LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);

        List<Contact> contacts = contactService.getContactsByDateRange(userId, startDate, endDate);
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contacts fetched successfully", contacts));
    }

    /**
     * Get paginated contacts for a user.
     */
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<ApiResponse<Page<Contact>>> getPaginatedContacts(
            @PathVariable Long userId,
            Pageable pageable) {

        Page<Contact> contacts = contactService.getContactsByUserIdPaginated(userId, pageable);
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contacts fetched successfully", contacts,
                        contacts.getTotalPages(), contacts.getNumber(), (int) contacts.getTotalElements()));
    }

    /**
     * Get a contact by phone.
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByPhone(@PathVariable String phone) {
        List<Contact> contacts = contactService.getContactsByPhone(phone);
        if (!contacts.isEmpty()) {
            return ResponseEntity
                    .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contacts fetched successfully", contacts));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "No contacts found", null));
    }

    /**
     * Get contacts by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByEmail(@PathVariable String email) {
        List<Contact> contacts = contactService.getContactsByEmail(email);
        if (!contacts.isEmpty()) {
            return ResponseEntity
                    .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contacts fetched successfully", contacts));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "No contacts found", null));
    }

    /**
     * Delete a contact by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contact deleted successfully", null));
    }

    /**
     * Get a contact by user ID and contact ID
     */
    @GetMapping("/user/{userId}/contact/{contactId}")
    public ResponseEntity<ApiResponse<Optional<Contact>>> getContactByUserIdAndContactId(
            @PathVariable Long userId,
            @PathVariable Long contactId) {

        Optional<Contact> contact = contactService.getContactByUserIdAndContactId(userId, contactId);
        if (contact.isPresent()) {
            return ResponseEntity
                    .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contact fetched successfully", contact));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "Contact not found", null));
    }

}
