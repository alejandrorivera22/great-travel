package com.alex.great_travel.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "customer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerEntity {

    @Id
    private String dni;
    @Column(length = 50)
    private String username;
    private String email;
    private String password;
    @Column(length = 20)
    private String creditCard;
    @Column(length = 12)
    private String phoneNumber;
    @Builder.Default
    private Integer totalFlights = 0;
    @Builder.Default
    private Integer totalLodgings = 0;
    @Builder.Default
    private Integer totalTours = 0;
    @Builder.Default
    private Boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "customer_roles",
            joinColumns = @JoinColumn(name = "customer_dni"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @Builder.Default
    private Set<TicketEntity> tickets = new HashSet<>();


    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @Builder.Default
    private Set<ReservationEntity> reservations = new HashSet<>();


    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    private Set<TourEntity> tours = new HashSet<>();

}
