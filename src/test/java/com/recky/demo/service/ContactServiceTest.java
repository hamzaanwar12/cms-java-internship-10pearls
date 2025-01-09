package com.recky.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.recky.demo.dao.ContactRepository;
import com.recky.demo.model.Contact;
import com.recky.demo.model.User;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private ContactService contactService;

    private Contact testContact;
    private User testUser;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== Initializing Test Setup ===");
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testContact = new Contact();
        testContact.setId(1L);
        testContact.setName("Test Contact");
        testContact.setPhone("1234567890");
        testContact.setEmail("contact@example.com");
        testContact.setUser(testUser);

        System.out.println("Created test contact with ID: " + testContact.getId() +
                ", Name: " + testContact.getName());
    }

    @Test
    void saveContact_Success() {
        System.out.println("\n=== Testing Contact Save Operation ===");
        System.out.println("Configuring mock repository to return test contact on save");
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        System.out.println("Attempting to save contact: " + testContact.getName());
        Contact savedContact = contactService.saveContact(testContact);

        System.out.println("Verifying save operation results...");
        assertNotNull(savedContact, "Saved contact should not be null");
        assertEquals(testContact.getName(), savedContact.getName(), "Name should match original");
        verify(contactRepository).save(any(Contact.class));
        System.out.println("Contact saved successfully with name: " + savedContact.getName());
    }

    @Test
    void getContactById_Found() {
        System.out.println("\n=== Testing Get Contact By ID (Found) ===");
        System.out.println("Configuring mock repository to return test contact for ID: " +
                testContact.getId());
        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));

        System.out.println("Attempting to find contact by ID: " + testContact.getId());
        Optional<Contact> found = contactService.getContactById(1L);

        System.out.println("Verifying found contact details...");
        assertTrue(found.isPresent(), "Contact should be found");
        assertEquals(testContact.getName(), found.get().getName(), "Name should match");
        System.out.println("Contact found successfully with name: " + found.get().getName());
    }

    @Test
    void getContactsByUserId_Success() {
        System.out.println("\n=== Testing Get Contacts By User ID ===");
        List<Contact> contacts = Arrays.asList(testContact);
        System.out.println("Configuring mock repository to return list of contacts");
        when(contactRepository.findAllByUserId(testUser.getId())).thenReturn(contacts);

        System.out.println("Attempting to retrieve contacts for user: " + testUser.getId());
        List<Contact> foundContacts = contactService.getContactsByUserId(testUser.getId());

        System.out.println("Verifying retrieved contacts...");
        assertFalse(foundContacts.isEmpty(), "Contact list should not be empty");
        assertEquals(1, foundContacts.size(), "Should have found 1 contact");
        System.out.println("Successfully retrieved " + foundContacts.size() + " contacts");
    }

    // @Test
    // void deleteContact_Success() {
    // System.out.println("\n=== Testing Delete Contact ===");
    // Long contactId = 1L;
    // Long userId = 1L;
    // System.out.println("Configuring mock repository for delete operation");
    // doNothing().when(contactRepository).deleteById(contactId);
    // doNothing().when(activityLogService).logActivity(any(), any(), any(), any());

    // System.out.println("Attempting to delete contact...");
    // contactService.deleteContact(contactId, userId);

    // System.out.println("Verifying delete operation was called...");
    // verify(contactRepository).deleteById(contactId);
    // verify(activityLogService).logActivity(eq(userId), eq("DELETE"), eq(userId),
    // any());
    // System.out.println("Contact deletion operation completed successfully");
    // }
    @Test
    void deleteContact_Success() {
        // Arrange
        Long contactId = 1L;
        Long userId = 1L;

        // No need for doNothing for void methods; focus on interaction verification

        // Act: Call the service method
        contactService.deleteContact(contactId, userId);

        // Assert: Verify interactions with mocks
        verify(contactRepository, times(1)).deleteById(contactId);
        verify(activityLogService, times(1)).logActivity(eq(userId), eq("DELETE"), eq(userId), anyString());
    }

    @Test
    void getContactsByDateRange_Success() {
        System.out.println("\n=== Testing Get Contacts By Date Range ===");
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<Contact> contacts = Arrays.asList(testContact);

        System.out.println("Configuring mock repository to return contacts within date range");
        when(contactRepository.findByUserIdAndCreatedAtBetween(
                testUser.getId(), startDate, endDate)).thenReturn(contacts);

        System.out.println("Attempting to retrieve contacts within date range...");
        List<Contact> foundContacts = contactService.getContactsByDateRange(
                testUser.getId(), startDate, endDate);

        System.out.println("Verifying retrieved contacts...");
        assertFalse(foundContacts.isEmpty(), "Should find contacts within date range");
        assertEquals(1, foundContacts.size(), "Should have found 1 contact");
        System.out.println("Successfully retrieved contacts within date range");
    }

    @Test
    void getContactsByUserIdPaginated_Success() {
        System.out.println("\n=== Testing Get Contacts By User ID Paginated ===");
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Contact> contactPage = new PageImpl<>(Arrays.asList(testContact));

        System.out.println("Configuring mock repository to return paginated contacts");
        when(contactRepository.findByUserId(testUser.getId(), pageRequest))
                .thenReturn(contactPage);

        System.out.println("Attempting to retrieve paginated contacts...");
        Page<Contact> foundContacts = contactService.getContactsByUserIdPaginated(
                testUser.getId(), pageRequest);

        System.out.println("Verifying paginated results...");
        assertTrue(foundContacts.hasContent(), "Should have contacts in page");
        assertEquals(1, foundContacts.getContent().size(), "Should have 1 contact in page");
        System.out.println("Successfully retrieved paginated contacts");
    }

    @Test
    void getContactsByPhone_Success() {
        System.out.println("\n=== Testing Get Contacts By Phone ===");
        List<Contact> contacts = Arrays.asList(testContact);
        String phone = "1234567890";

        System.out.println("Configuring mock repository         to return contacts by phone");
        when(contactRepository.findByPhone(phone)).thenReturn(contacts);

        System.out.println("Attempting to retrieve contacts by phone: " + phone);
        List<Contact> foundContacts = contactService.getContactsByPhone(phone);

        System.out.println("Verifying retrieved contacts by phone...");
        assertFalse(foundContacts.isEmpty(), "Contact list should not be empty");
        assertEquals(1, foundContacts.size(), "Should have found 1 contact");
        assertEquals(phone, foundContacts.get(0).getPhone(), "Phone number should match");
        System.out.println("Successfully retrieved contacts by phone");
    }

    @Test
    void getContactsByEmail_Success() {
        System.out.println("\n=== Testing Get Contacts By Email ===");
        List<Contact> contacts = Arrays.asList(testContact);
        String email = "contact@example.com";

        System.out.println("Configuring mock repository to return contacts by email");
        when(contactRepository.findByEmail(email)).thenReturn(contacts);

        System.out.println("Attempting to retrieve contacts by email: " + email);
        List<Contact> foundContacts = contactService.getContactsByEmail(email);

        System.out.println("Verifying retrieved contacts by email...");
        assertFalse(foundContacts.isEmpty(), "Contact list should not be empty");
        assertEquals(1, foundContacts.size(), "Should have found 1 contact");
        assertEquals(email, foundContacts.get(0).getEmail(), "Email should match");
        System.out.println("Successfully retrieved contacts by email");
    }

    @Test
    void existsByPhoneAndUserId_Success() {
        System.out.println("\n=== Testing Exists By Phone And User ID ===");
        String phone = "1234567890";
        Long userId = 1L;

        System.out.println("Configuring mock repository to return true for existence check");
        when(contactRepository.existsByPhoneAndUserId(phone, userId)).thenReturn(true);

        System.out.println("Checking existence of contact with phone and user ID...");
        boolean exists = contactService.existsByPhoneAndUserId(phone, userId);

        System.out.println("Verifying existence check...");
        assertTrue(exists, "Contact should exist with given phone and user ID");
        System.out.println("Existence verified successfully");
    }

    @Test
    void existsByIdAndUserId_Success() {
        System.out.println("\n=== Testing Exists By ID And User ID ===");
        Long id = 1L;
        Long userId = 1L;

        System.out.println("Configuring mock repository to return true for existence check");
        when(contactRepository.existsByIdAndUserId(id, userId)).thenReturn(true);

        System.out.println("Checking existence of contact by ID and user ID...");
        boolean exists = contactService.existsByIdAndUserId(id, userId);

        System.out.println("Verifying existence check...");
        assertTrue(exists, "Contact should exist with given ID and user ID");
        System.out.println("Existence verified successfully");
    }

    @Test
    void getContactsByDateRangePaginated_Success() {
        System.out.println("\n=== Testing Get Contacts By Date Range Paginated ===");
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Contact> contactPage = new PageImpl<>(Arrays.asList(testContact));

        System.out.println("Configuring mock repository to return paginated contacts by date range");
        when(contactRepository.findByUserIdAndCreatedAtBetween(
                testUser.getId(), startDate, endDate, pageRequest)).thenReturn(contactPage);

        System.out.println("Attempting to retrieve paginated contacts by date range...");
        Page<Contact> foundContacts = contactService.getContactsByDateRangePaginated(
                testUser.getId(), startDate, endDate, page, size);

        System.out.println("Verifying retrieved paginated contacts...");
        assertTrue(foundContacts.hasContent(), "Page should have content");
        assertEquals(1, foundContacts.getContent().size(), "Should have 1 contact in page");
        System.out.println("Successfully retrieved paginated contacts by date range");
    }
}
