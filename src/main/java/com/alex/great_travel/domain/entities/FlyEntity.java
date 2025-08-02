package com.alex.great_travel.domain.entities;

import com.alex.great_travel.util.AeroLine;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity(name = "fly")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FlyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double originLat;
    private Double originLng;
    private Double destinyLat;
    private Double destinyLng;
    @Column(length = 20)
    private String originName;
    @Column(length = 20)
    private String destinyName;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private AeroLine aeroLine;

    @OneToMany(mappedBy = "fly",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    private Set<TicketEntity> tickets;

}
