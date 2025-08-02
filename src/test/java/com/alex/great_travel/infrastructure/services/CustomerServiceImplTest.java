package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.models.request.CustomerRequest;
import com.alex.great_travel.api.models.request.CustomerUpdateRequest;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.domain.entities.CustomerEntity;
import com.alex.great_travel.domain.entities.RoleEntity;
import com.alex.great_travel.domain.repositories.CustomerRepository;
import com.alex.great_travel.domain.repositories.RoleRepository;
import com.alex.great_travel.util.Role;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceImplTest extends ServiceSpec{

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    CustomerEntity customer;
    RoleEntity role;
    String customerDni;

    @BeforeEach
    void setUp() {
        role = DummyData.createRoleEntityCustomer();
        customer = DummyData.createCustomerEntity(role);
        customerDni = customer.getDni();
    }


    @Test
    @DisplayName("should create a customer and return a response")
    void create_ShouldSaveCustoomerAndReturnResponse() {
        CustomerRequest request = DummyData.createCustomerRequest();

        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer);

        CustomerResponse response = customerService.create(request);

        assertNotNull(response);
        assertEquals("dummy_user", response.getUsername());
        verify(customerRepository).save(any(CustomerEntity.class));
    }



    @Test
    @DisplayName("happy path should return customer when it exists")
    void read_ShouldReturnCustomer_WhenExists() {
        when(customerRepository.findById(customerDni)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.read(customerDni);
        assertNotNull(response);

        assertAll(
                () -> assertEquals(customer.getDni(), response.getDni()),
                () -> assertEquals(customer.getUsername(), response.getUsername()),
                () -> assertEquals(customer.getEmail(), response.getEmail())
        );
    }

    @Test
    @DisplayName("Unhappy path Should throw IdNotFoundException")
    void read_ShouldThrowException_WhenCustomerNotExists() {
        when(customerRepository.findById(customerDni)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> customerService.read(customerDni));
    }

    @Test
    @DisplayName("Should update a customer given a valid request and customer DNI")
    void update_ShouldReturnUpdateCustomer() {
        when(customerRepository.findById(customerDni)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.read(customerDni);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(customer.getDni(), response.getDni()),
                () -> assertEquals(customer.getUsername(), response.getUsername()),
                () -> assertEquals(customer.getEmail(), response.getEmail())
        );

        CustomerUpdateRequest updateRequest = DummyData.createCustomerUpdateRequest();
        CustomerResponse updateResponse = customerService.update(updateRequest, customerDni);

        assertNotNull(updateResponse);
        assertAll(
                () -> assertEquals(customerDni, updateResponse.getDni()),
                () -> assertEquals(updateRequest.getPhoneNumber(), updateResponse.getPhoneNumber())
        );
    }

    @Test
    void updateUsername() {
        when(customerRepository.findById(customerDni)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.read(customerDni);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(customer.getDni(), response.getDni()),
                () -> assertEquals(customer.getUsername(), response.getUsername()),
                () -> assertEquals(customer.getEmail(), response.getEmail())
        );

        String newUsername = "new username";
        CustomerResponse updateResponse = customerService.updateUsername(customerDni, newUsername);
        assertNotNull(updateResponse);
        assertAll(
                () -> assertEquals(customerDni, updateResponse.getDni()),
                () -> assertEquals("new username", updateResponse.getUsername())
        );

    }


    @Test
    @DisplayName("Should add role to customer when role and customer exist and are valid")
    void addRole_ShouldAddRoleToCustomer_WhenValid() {
        Role newRole = Role.ADMIN;
        RoleEntity role = DummyData.createRoleEntityAdmin();

        when(customerRepository.findByUsername(customer.getUsername()))
                .thenReturn(Optional.of(customer));

        when(roleRepository.findByName(newRole)).thenReturn(Optional.of(role));

        when(customerRepository.save(any(CustomerEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.addRole(customer.getUsername(), newRole);

        assertNotNull(response);
        assertTrue(response.getRoles().contains(newRole.name()));
    }
}