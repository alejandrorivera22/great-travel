package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.api.models.request.CustomerRequest;
import com.alex.great_travel.api.models.request.CustomerUpdateRequest;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.domain.entities.CustomerEntity;
import com.alex.great_travel.domain.entities.RoleEntity;
import com.alex.great_travel.domain.repositories.CustomerRepository;
import com.alex.great_travel.domain.repositories.RoleRepository;
import com.alex.great_travel.infrastructure.abstractService.CustomerService;
import com.alex.great_travel.util.Role;
import com.alex.great_travel.util.Tables;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepositroy;
    private static final Set<Role> VALID_ROLES = Set.of(Role.CUSTOMER, Role.ADMIN);
    private final PasswordEncoder encoder;

    @Override
    public CustomerResponse create(CustomerRequest customerRequest) {

        if (this.customerRepository.existsByDni(customerRequest.getDni())) {
            throw new IllegalArgumentException("DNI already exists");
        }

        if (this.customerRepository.existsByUsername(customerRequest.getUsername())){
            throw new IllegalArgumentException("Username already exists: " + customerRequest.getUsername());
        }

        if (this.customerRepository.existsByEmail(customerRequest.getEmail())){
            throw new IllegalArgumentException("Email already exists: " + customerRequest.getEmail());
        }

        RoleEntity role = this.roleRepositroy.findByName(Role.CUSTOMER)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        CustomerEntity customerToPersist = CustomerEntity
                .builder()
                .dni(customerRequest.getDni())
                .username(customerRequest.getUsername())
                .email(customerRequest.getEmail())
                .password(encoder.encode(customerRequest.getPassword()))
                .creditCard(customerRequest.getCreditCard())
                .phoneNumber(customerRequest.getPhoneNumber())
                .roles(Set.of(role))
                .build();

        CustomerEntity customerPersisted = this.customerRepository.save(customerToPersist);

        return this.entityToResponse(customerPersisted);
    }

    @Override
    public CustomerResponse read(String dni) {
        CustomerEntity customer = customerRepository.findById(dni)
                .orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        return entityToResponse(customer);
    }

    @Override
    public CustomerResponse update(CustomerUpdateRequest customerRequest, String dni) {
        CustomerEntity customerToUpdate = customerRepository.findById(dni)
                .orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        customerToUpdate.setCreditCard(customerRequest.getCreditCard());
        customerToUpdate.setPhoneNumber(customerRequest.getPhoneNumber());

        CustomerEntity customerUpdated = customerRepository.save(customerToUpdate);
        return entityToResponse(customerUpdated);
    }

    @Override
    public CustomerResponse updateUsername(String dni, String username) {
        CustomerEntity customerToUpdate = customerRepository.findById(dni)
                .orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        customerToUpdate.setUsername(username);

        CustomerEntity customerUpdated = customerRepository.save(customerToUpdate);
        return entityToResponse(customerUpdated);
    }

    @Override
    public CustomerResponse updatePassword(String dni, String password) {
        CustomerEntity customerToUpdate = customerRepository.findById(dni)
                .orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        customerToUpdate.setPassword(encoder.encode(password));

        CustomerEntity customerUpdated = customerRepository.save(customerToUpdate);
        return entityToResponse(customerUpdated);
    }

    @Override
    public CustomerResponse addRole(String username, Role role) {
        if (!VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("Role does not exist: " + role.name());
        }

        CustomerEntity customerToUpdate = this.customerRepository.findByUsername(username)
                .orElseThrow(() -> new IdNotFoundException("Username not found"));

        RoleEntity roleEntity = this.roleRepositroy.findByName(role)
                .orElseThrow(() -> new IllegalArgumentException("Role not found in DB: " + role.name()));

        if (customerToUpdate.getRoles() == null) {
            customerToUpdate.setRoles(new HashSet<>());
        }

        customerToUpdate.getRoles().add(roleEntity);

        CustomerEntity customerUpdated = this.customerRepository.save(customerToUpdate);

        return entityToResponse(customerUpdated);
    }

    private CustomerResponse entityToResponse(CustomerEntity customerEntity){
        CustomerResponse response = new CustomerResponse();
        BeanUtils.copyProperties(customerEntity, response);
        response.setRoles(customerEntity.getRoles().stream().map(role -> role.getName().name()).toList());
        return response;
    }
}
