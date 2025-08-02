package com.alex.great_travel.domain.repositories;

import com.alex.great_travel.domain.entities.CustomerEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest extends RepositorySpec {

    @Autowired
    private CustomerRepository customerRepository;
    private static final String VALID_USERNAME = "john_doe";
    private static final String VALID_EMAIL = "john@example.com";

    @Test
    @DisplayName("should return a customer when username exists")
    void findByUsername_ShouldReturnCustomer_WhenUsernameExists() {
        Optional<CustomerEntity> customer = this.customerRepository.findByUsername(VALID_USERNAME);
        assertTrue(customer.isPresent(), "Customer shoul to be present");
        assertEquals(VALID_USERNAME, customer.get().getUsername(), "Customer username should be " + VALID_USERNAME);
    }

    @Test
    @DisplayName("existsByUsername should return true when username exists")
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        boolean exists = customerRepository.existsByUsername(VALID_USERNAME);
        assertTrue(exists, "Should be true because username exists");
    }

    @Test
    @DisplayName("existsByEmail should return true when email exists")
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        boolean exists = customerRepository.existsByEmail(VALID_EMAIL);
        assertTrue(exists, "Should be true because email exists");
    }

    @Test
    @DisplayName("existsByDni should return true when DNI exists")
    void existsByDni_ShouldReturnTrue_WhenDniExists() {
        String dni = "VIKI771012HMCRG093";
        boolean exists = customerRepository.existsByDni(dni);
        assertTrue(exists, "Should be true because dni exists");
    }
}