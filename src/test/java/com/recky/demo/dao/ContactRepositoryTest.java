package com.recky.demo.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.recky.demo.model.Contact;
import com.recky.demo.model.User;

@DataJpaTest
public class ContactRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;

    private Contact testContact;
    private User testUser;
    private String userId = "some-random-id";

    @BeforeEach
    void setUp() {
        System.out.println("\n=== Setting up new test contact and user ===");

        // Create test user with a String ID
        // testUser = new User();
        // testUser.setUsername("testuser");
        // testUser.setEmail("test@example.com");
        // testUser.setPassword("password");
        // testUser.setName("Test User");



        testUser = new User();
        // testUser.setId(UUID.randomUUID().toString()); // Manually set UUID
        testUser.setId(userId); // Manually set UUID
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        // testUser.setPassword("password");
        // testUser.setName("Test User");

        testUser = entityManager.persist(testUser);

        // Create test contact with userId as a String
        testContact = new Contact();
        testContact.setName("Test Contact");
        testContact.setPhone("1234567890");
        testContact.setEmail("contact@example.com");
        testContact.setAddress("123 Test St");
        testContact.setUser(testUser);

        System.out.println("Created test contact with name: " + testContact.getName() +
                " and phone: " + testContact.getPhone());
    }

    @Test
    void whenFindById_thenReturnContact() {
        System.out.println("\n=== Testing findById with valid ID ===");
        System.out.println("Persisting test contact to database...");
        Contact persistedContact = entityManager.persist(testContact);
        entityManager.flush();
        System.out.println("Contact persisted with ID: " + persistedContact.getId());

        System.out.println("Attempting to find contact by ID: " + persistedContact.getId());
        Optional<Contact> found = contactRepository.findById(persistedContact.getId());

        System.out.println("Verifying contact was found and matches expected data...");
        assertTrue(found.isPresent(), "Contact should be found in database");
        assertEquals(testContact.getName(), found.get().getName(), "Name should match original");
        assertEquals(testContact.getPhone(), found.get().getPhone(), "Phone should match original");
        System.out.println("Test completed successfully - Contact found by ID");
    }

    @Test
    void whenFindAllByUserId_thenReturnContacts() {
        System.out.println("\n=== Testing findAllByUserId ===");
        System.out.println("Persisting test contact to database...");
        entityManager.persist(testContact);
        entityManager.flush();

        System.out.println("Searching for contacts with userId: " + testUser.getId());
        List<Contact> found = contactRepository.findAllByUserId(testUser.getId());

        System.out.println("Verifying contacts were found...");
        assertFalse(found.isEmpty(), "Contact list should not be empty");
        assertEquals(1, found.size(), "Should find exactly one contact");
        assertEquals(testContact.getName(), found.get(0).getName(), "Contact name should match");
        System.out.println("Test completed successfully - Contacts found by userId");
    }

    @Test
    void whenFindByPhone_thenReturnContacts() {
        System.out.println("\n=== Testing findByPhone ===");
        System.out.println("Persisting test contact to database...");
        entityManager.persist(testContact);
        entityManager.flush();

        System.out.println("Searching for contact with phone: " + testContact.getPhone());
        List<Contact> found = contactRepository.findByPhone(testContact.getPhone());

        System.out.println("Verifying contact data matches...");
        assertFalse(found.isEmpty(), "Contact should be found by phone");
        assertEquals(testContact.getPhone(), found.get(0).getPhone(), "Phone should match");
        System.out.println("Test completed successfully - Contact found by phone");
    }

    @Test
    void whenFindByUserIdAndDateRange_thenReturnContacts() {
        System.out.println("\n=== Testing findByUserIdAndCreatedAtBetween ===");
        System.out.println("Persisting test contact to database...");
        entityManager.persist(testContact);
        entityManager.flush();

        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        System.out.println("Searching for contacts within date range");
        List<Contact> found = contactRepository.findByUserIdAndCreatedAtBetween(
                testUser.getId(), startDate, endDate);

        System.out.println("Verifying contacts were found...");
        assertFalse(found.isEmpty(), "Should find contacts within date range");
        assertEquals(testContact.getName(), found.get(0).getName(), "Contact name should match");
        System.out.println("Test completed successfully - Contacts found within date range");
    }

    @Test
    void whenFindByUserIdPaginated_thenReturnContactPage() {
        System.out.println("\n=== Testing findByUserId with pagination ===");
        System.out.println("Persisting test contact to database...");
        entityManager.persist(testContact);
        entityManager.flush();

        System.out.println("Retrieving paginated contacts...");
        Page<Contact> contactPage = contactRepository.findByUserId(
                testUser.getId(), PageRequest.of(0, 10));

        System.out.println("Verifying paginated results...");
        assertTrue(contactPage.hasContent(), "Page should contain contacts");
        assertEquals(1, contactPage.getContent().size(), "Should have one contact");
        assertEquals(testContact.getName(), contactPage.getContent().get(0).getName(),
                "Contact name should match");
        System.out.println("Test completed successfully - Paginated contacts retrieved");
    }

    @Test
    void whenExistsByPhone_thenReturnTrue() {
        System.out.println("\n=== Testing existsByPhone ===");
        System.out.println("Persisting test contact to database...");
        entityManager.persist(testContact);
        entityManager.flush();

        System.out.println("Checking if contact exists with phone: " + testContact.getPhone());
        boolean exists = contactRepository.existsByPhone(testContact.getPhone());

        System.out.println("Verifying contact exists...");
        assertTrue(exists, "Contact should exist with given phone number");
        System.out.println("Test completed successfully - Contact existence verified");
    }

    @Test
    void whenFindByUserIdAndId_thenReturnContact() {
        System.out.println("\n=== Testing findByUserIdAndId ===");
        System.out.println("Persisting test contact to database...");
        Contact persistedContact = entityManager.persist(testContact);
        entityManager.flush();

        System.out.println("Searching for contact with userId and contactId");
        Optional<Contact> found = contactRepository.findByUserIdAndId(
                testUser.getId(), persistedContact.getId());

        System.out.println("Verifying contact was found...");
        assertTrue(found.isPresent(), "Contact should be found");
        assertEquals(testContact.getName(), found.get().getName(), "Contact name should match");
        System.out.println("Test completed successfully - Contact found by userId and id");
    }
}
