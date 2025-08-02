package com.alex.great_travel.domain.repositories;

import com.alex.great_travel.domain.entities.RoleEntity;
import com.alex.great_travel.util.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoleRepositoryTest extends RepositorySpec{


    @Autowired
    RoleRepository roleRepository;

    @Test
    @DisplayName("findByName should return a role when name exists")
    void findByName_ShouldReturnRole_WhenRoleNameExists() {
        Optional<RoleEntity> result = this.roleRepository.findByName(Role.CUSTOMER);
        assertTrue(result.isPresent(), "Expected role to be present");
        assertEquals(Role.CUSTOMER, result.get().getName(), "Role does not match");
    }
}