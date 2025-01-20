package com.recky.demo.api;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recky.demo.model.Contact;
import com.recky.demo.model.User;
import com.recky.demo.service.ActivityLogService;
import com.recky.demo.service.ContactService;
import com.recky.demo.service.UserService;
import com.recky.demo.util.ApiResponse;

class ContactControllerTest {

    @Mock
    private ContactService contactService;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ContactController contactController;

    private Contact testContact;
    private User testUser;
    private String userId = "some-random-id";

    @BeforeEach
    void setUp() {
        System.out.println("\n=== Initializing Test Setup ===");
        MockitoAnnotations.openMocks(this);

        // Initialize test user
        testUser = new User();
        testUser.setId(userId); // Manually set UUID
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        // testUser.setPassword("password");
        // testUser.setName("Test User");

        // Initialize test contact
        testContact = new Contact();
        testContact.setId(1L);
        testContact.setPhone("1234567890");
        testContact.setEmail("test@example.com");
        testContact.setUser(testUser);
        testContact.setCreatedAt(LocalDateTime.now());

        System.out.println("Test setup completed");
    }

    @Test
    void testGetContactById_Success() {
        System.out.println("\n=== Testing Get Contact By ID Success ===");
        Long contactId = 1L;

        when(contactService.getContactById(contactId)).thenReturn(Optional.of(testContact));

        ResponseEntity<ApiResponse<Optional<Contact>>> response = contactController.getContactById(contactId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contact fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testGetContactsByPhone_Success() {
        System.out.println("\n=== Testing Get Contacts By Phone Success ===");
        String phone = "1234567890";
        List<Contact> contacts = Arrays.asList(testContact);

        when(contactService.getContactsByPhone(phone)).thenReturn(contacts);

        ResponseEntity<ApiResponse<List<Contact>>> response = contactController.getContactsByPhone(phone);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contacts fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testGetContactsByEmail_Success() {
        System.out.println("\n=== Testing Get Contacts By Email Success ===");
        String email = "test@example.com";
        List<Contact> contacts = Arrays.asList(testContact);

        when(contactService.getContactsByEmail(email)).thenReturn(contacts);

        ResponseEntity<ApiResponse<List<Contact>>> response = contactController.getContactsByEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contacts fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testCreateContact_Success() {
        System.out.println("\n=== Testing Create Contact Success ===");
        Contact inputContact = new Contact();
        inputContact.setPhone("1234567890");
        inputContact.setEmail("test@example.com");

        when(userService.getUserByIdOrThrow(userId)).thenReturn(testUser);
        when(contactService.existsByPhoneAndUserId(inputContact.getPhone(), userId)).thenReturn(false);
        when(contactService.saveContact(any(Contact.class))).thenReturn(testContact);

        ResponseEntity<ApiResponse<Contact>> response = contactController.createContact(inputContact, userId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contact created successfully", response.getBody().getMessage());
    }

    @Test
    void testUpdateContact_Success() {
        System.out.println("\n=== Testing Update Contact Success ===");
        Long contactId = 1L;
        Contact updateContact = new Contact();
        updateContact.setPhone("9876543210");
        updateContact.setEmail("updated@example.com");

        when(contactService.findById(contactId)).thenReturn(Optional.of(testContact));
        when(contactService.existsByPhoneAndIdNot(updateContact.getPhone(), contactId)).thenReturn(false);
        when(contactService.saveContact(any(Contact.class))).thenReturn(updateContact);

        ResponseEntity<ApiResponse<Contact>> response = contactController.updateContact(contactId, updateContact,
                userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contact updated successfully", response.getBody().getMessage());
    }

    @Test
    void testDeleteContact_Success() {
        System.out.println("\n=== Testing Delete Contact Success ===");
        Long contactId = 1L;

        when(contactService.existsById(contactId)).thenReturn(true);
        doNothing().when(contactService).deleteContact(contactId, userId);

        ResponseEntity<ApiResponse<Void>> response = contactController.deleteContact(userId, contactId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contact deleted successfully", response.getBody().getMessage());
    }

    @Test
    void testGetContactsByUserId_Success() {
        System.out.println("\n=== Testing Get Contacts By User ID Success ===");
        List<Contact> contacts = Arrays.asList(testContact);
        Optional<String> email = Optional.empty();
        Optional<String> phone = Optional.empty();

        when(contactService.getContactsByUserIdWithFilters(userId, email, phone)).thenReturn(contacts);

        ResponseEntity<ApiResponse<List<Contact>>> response = contactController.getContactsByUserId(userId, email,
                phone);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contacts fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testGetPaginatedContacts_Success() {
        System.out.println("\n=== Testing Get Paginated Contacts Success ===");
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = Arrays.asList(testContact);
        Page<Contact> contactPage = new PageImpl<>(contacts, pageable, contacts.size());

        when(contactService.getContactsByUserIdPaginated(userId, pageable)).thenReturn(contactPage);

        ResponseEntity<ApiResponse<Page<Contact>>> response = contactController.getPaginatedContacts(userId, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contacts fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testGetContactByUserIdAndContactId_Success() {
        System.out.println("\n=== Testing Get Contact By User ID and Contact ID Success ===");
        Long contactId = 1L;

        when(contactService.getContactByUserIdAndContactId(userId, contactId)).thenReturn(Optional.of(testContact));

        ResponseEntity<ApiResponse<Optional<Contact>>> response = contactController
                .getContactByUserIdAndContactId(userId, contactId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contact fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testGetContactsByDateRange_Success() {
        System.out.println("\n=== Testing Get Contacts By Date Range Success ===");
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        List<Contact> contacts = Arrays.asList(testContact);

        when(contactService.getContactsByDateRange(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(contacts);

        ResponseEntity<ApiResponse<List<Contact>>> response = contactController.getContactsByDateRange(userId,
                startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Contacts fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testGetContactsByDateRangePaginated_Success() {
        System.out.println("\n=== Testing Get Contacts By Date Range Paginated Success ===");
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        int page = 0;
        int size = 10;
        List<Contact> contacts = Arrays.asList(testContact);
        Page<Contact> contactPage = new PageImpl<>(contacts, PageRequest.of(page, size), contacts.size());

        when(contactService.getContactsByDateRangePaginated(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(page), eq(size)))
                .thenReturn(contactPage);

        ResponseEntity<ApiResponse<Page<Contact>>> response = contactController.getContactsByDateRangePaginated(userId,
                startDate, endDate, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Paginated contacts fetched successfully", response.getBody().getMessage());
    }
}