package com.alex.great_travel.domain.repositories;

import com.alex.great_travel.domain.entities.RoleEntity;
import com.alex.great_travel.util.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<RoleEntity, Short> {
    Optional<RoleEntity> findByName(Role name);
}
