package com.alex.great_travel.domain.entities;

import com.alex.great_travel.util.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role name;
}
