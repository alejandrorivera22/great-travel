package com.alex.great_travel.infrastructure.helpers;

import com.alex.great_travel.domain.entities.*;
import com.alex.great_travel.domain.repositories.ReservationRepository;
import com.alex.great_travel.domain.repositories.TicketRepository;
import com.alex.great_travel.infrastructure.services.ReservationServiceImp;
import com.alex.great_travel.infrastructure.services.TicketServiceImpl;
import com.alex.great_travel.util.TravelUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Transactional
@Component
@AllArgsConstructor
public class TourHelper {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;

    public Set<TicketEntity> createTickets(Set<FlyEntity> flights, CustomerEntity customer) {

        HashSet<TicketEntity> response = new HashSet<TicketEntity>(flights.size());
        flights.forEach(fly -> {
            TicketEntity ticketToPersist = TicketEntity.builder()
                    .id(UUID.randomUUID())
                    .fly(fly)
                    .customer(customer)
                    .price(fly.getPrice().multiply(TicketServiceImpl.CHARGER_PRICE_PERCENTAGE))
                    .purchaseDate(LocalDate.now())
                    .arrivalDate(TravelUtil.getRandomLatter())
                    .departureDate(TravelUtil.getRandomSoon())
                    .build();

            response.add(this.ticketRepository.save(ticketToPersist));
        });

        return response;

    }

    public Set<ReservationEntity> createReservations(HashMap<HotelEntity, Integer> hotels, CustomerEntity customer) {

        HashSet<ReservationEntity> response = new HashSet<>(hotels.size());
        hotels.forEach((hotel, totalDays) -> {
            ReservationEntity reservationToPersist = ReservationEntity.builder()
                    .id(UUID.randomUUID())
                    .hotel(hotel)
                    .customer(customer)
                    .totalDays(totalDays)
                    .dateTimeReservation(LocalDateTime.now())
                    .dateStart(LocalDate.now())
                    .dateEnd(LocalDate.now().plusDays(totalDays))
                    .price(hotel.getPrice().multiply(ReservationServiceImp.CHARGER_PRICE_PERCENTAGE))
                    .build();

            response.add(this.reservationRepository.save(reservationToPersist));

        });

        return response;

    }

    public TicketEntity createTicket(FlyEntity fly , CustomerEntity customer){

        TicketEntity ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().multiply(TicketServiceImpl.CHARGER_PRICE_PERCENTAGE))
                .purchaseDate(LocalDate.now())
                .arrivalDate(TravelUtil.getRandomLatter())
                .departureDate(TravelUtil.getRandomSoon())
                .build();

        return this.ticketRepository.save(ticketToPersist);

    }

    public ReservationEntity createReservation(HotelEntity hotel, CustomerEntity customer, Integer totalDays){
        ReservationEntity reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .hotel(hotel)
                .customer(customer)
                .totalDays(totalDays)
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(totalDays))
                .price(hotel.getPrice().multiply(ReservationServiceImp.CHARGER_PRICE_PERCENTAGE))
                .build();

        return this.reservationRepository.save(reservationToPersist);
    }

}
