package com.recky.demo.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.recky.demo.exception.UserNotFoundException;
import com.recky.demo.model.Contact;
import com.recky.demo.model.User;
import com.recky.demo.service.ActivityLogService;
import com.recky.demo.service.ContactService;
import com.recky.demo.service.UserService;
import com.recky.demo.util.ApiResponse;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private ActivityLogService activityLogService; // Inject ActivityLogService

    @Autowired
    private UserService userService; // Inject ActivityLogService

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

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

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Contact>> createContact(@RequestBody Contact contact, @RequestParam Long userId) {
        try {
            // Log the userId
            logger.info("Received userId: {}", userId);

            // Fetch the user from the database
            User user = userService.getUserByIdOrThrow(userId);

            if (contactService.existsByPhoneAndUserId(contact.getPhone(), userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), "error",
                                "You already have a contact with this phone number", null));
            }

            contact.setUser(user);

            logger.info("Contact after setUser: {}", contact);

            // Save the contact
            Contact savedContact = contactService.saveContact(contact);

            activityLogService.logActivity(userId, "CREATE", userId,
                    "Created contact with phone: " + contact.getPhone());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(HttpStatus.CREATED.value(), "success", "Contact created successfully",
                            savedContact));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "User not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                            "An error occurred while creating the contact", null));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Contact>> updateContact(@PathVariable Long id, @RequestBody Contact contact,
            @RequestParam Long userId) {
        try {
            Optional<Contact> existingContact = contactService.findById(id);
            if (!existingContact.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "Contact not found", null));
            }
            if (contactService.existsByPhoneAndIdNot(contact.getPhone(), id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), "error",
                                "Another contact with this phone number already exists", null));
            }
            contact.setId(id);
            Contact updatedContact = contactService.saveContact(contact);

            // Log activity
            activityLogService.logActivity(userId, "UPDATE", userId, "Updated contact with ID: " + id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contact updated successfully",
                            updatedContact));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                            "An error occurred while updating the contact", null));
        }
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long userId, @PathVariable Long id) {
        try {
            // Log the incoming request details
            logger.info("Received delete request for contact ID: {} by user ID: {}", id, userId);

            // Check if the contact exists
            if (!contactService.existsById(id)) {
                logger.warn("Contact with ID: {} does not exist", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "Contact not found", null));
            }

            // Perform the deletion
            contactService.deleteContact(id,userId);
            // logger.info("Contact with ID: {} deleted successfully", id);

            // // Log the activity
            // activityLogService.logActivity(userId, "DELETE", userId, "Deleted contact with ID: " + id);
            // logger.info("Activity log for delete action recorded.");

            return ResponseEntity
                    .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contact deleted successfully", null));
        } catch (Exception e) {
            // Log the exception
            logger.error("An error occurred while deleting the contact with ID: {} by user ID: {}", id, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                            "An error occurred while deleting the contact", null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByUserId(
            @PathVariable Long userId,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> phone) {

        List<Contact> contacts = contactService.getContactsByUserIdWithFilters(userId, email, phone);

        // Log activity
        activityLogService.logActivity(userId, "GET", userId, "Fetched contacts for userId: " + userId);

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contacts fetched successfully", contacts));
    }

    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<ApiResponse<Page<Contact>>> getPaginatedContacts(
            @PathVariable Long userId,
            Pageable pageable) {

        Page<Contact> contacts = contactService.getContactsByUserIdPaginated(userId, pageable);

        // Log activity
        activityLogService.logActivity(userId, "GET", userId, "Fetched paginated contacts for userId: " + userId);

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contacts fetched successfully", contacts,
                        contacts.getTotalPages(), contacts.getNumber(), (int) contacts.getTotalElements()));
    }

    /**
     * Get a contact by user ID and contact ID, with activity logging.
     */
    @GetMapping("/user/{userId}/contact/{contactId}")
    public ResponseEntity<ApiResponse<Optional<Contact>>> getContactByUserIdAndContactId(
            @PathVariable Long userId,
            @PathVariable Long contactId) {
        try {
            Optional<Contact> contact = contactService.getContactByUserIdAndContactId(userId, contactId);
            if (!contact.isPresent()) {
                activityLogService.logActivity(userId, "GET", userId, "Contact not found with ID: " + contactId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "error", "Contact not found", null));
            }

            activityLogService.logActivity(userId, "GET", userId, "Fetched contact with ID: " + contactId);
            return ResponseEntity
                    .ok(new ApiResponse<>(HttpStatus.OK.value(), "success", "Contact fetched successfully", contact));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                            "An error occurred while fetching the contact", null));
        }
    }

    /**
     * Get all contacts created within a specific date range for a user, with
     * activity logging.
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<ApiResponse<List<Contact>>> getContactsByDateRange(
            @PathVariable Long userId,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        try {
            LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);

            List<Contact> contacts = contactService.getContactsByDateRange(userId, startDate, endDate);

            activityLogService.logActivity(userId, "GET", userId,
                    "Fetched contacts from date range " + startDate + " to " + endDate);

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "Contacts fetched successfully", contacts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                            "An error occurred while fetching contacts", null));
        }
    }

    /**
     * Get paginated contacts created within a specific date range for a user, with
     * activity logging.
     */
    @GetMapping("/user/{userId}/date-range/paginated")
    public ResponseEntity<ApiResponse<Page<Contact>>> getContactsByDateRangePaginated(
            @PathVariable Long userId,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);

            Page<Contact> contacts = contactService.getContactsByDateRangePaginated(userId, startDate, endDate, page,
                    size);

            activityLogService.logActivity(userId, "GET", userId,
                    "Fetched paginated contacts from date range " + startDate + " to " + endDate);

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "success",
                    "Paginated contacts fetched successfully", contacts,
                    contacts.getTotalPages(), contacts.getNumber(), (int) contacts.getTotalElements()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error",
                            "An error occurred while fetching paginated contacts", null));
        }
    }

}