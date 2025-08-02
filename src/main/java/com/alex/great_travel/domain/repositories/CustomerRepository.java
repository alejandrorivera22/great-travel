package com.alex.great_travel.domain.repositories;

import com.alex.great_travel.domain.entities.CustomerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<CustomerEntity, String> {
    Optional<CustomerEntity> findByUsername(String username);
    boolean existsByUsername(String email);
    boolean existsByEmail(String email);
    boolean existsByDni(String email);
}
