package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.api.models.request.TourRequest;
import com.alex.great_travel.api.models.response.TourResponse;
import com.alex.great_travel.domain.entities.*;
import com.alex.great_travel.domain.repositories.CustomerRepository;
import com.alex.great_travel.domain.repositories.FlyRepository;
import com.alex.great_travel.domain.repositories.HotelRepository;
import com.alex.great_travel.domain.repositories.TourRepository;
import com.alex.great_travel.infrastructure.abstractService.TourService;
import com.alex.great_travel.infrastructure.helpers.CustomerHelper;
import com.alex.great_travel.infrastructure.helpers.TourHelper;
import com.alex.great_travel.util.Tables;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final FlyRepository flyRepository;
    private final TourHelper tourHelper;
    private final CustomerHelper customerHelper;

    @Override
    public TourResponse create(TourRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        Set<FlyEntity> flights = new HashSet<>();
        request.getFlights()
                .forEach(fly -> flights.add(this.flyRepository.findById(fly.getId())
                        .orElseThrow(() -> new IdNotFoundException(Tables.fly.name()))));

        HashMap<HotelEntity, Integer> hotels = new HashMap<>();
        request.getHotels()
                .forEach(hotel ->
                        hotels.put(this.hotelRepository.findById(hotel.getId())
                                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name())), hotel.getTotalDays()));

        TourEntity tourToSave = TourEntity.builder()
                .tickets(tourHelper.createTickets(flights, customer))
                .reservations(tourHelper.createReservations(hotels, customer))
                .customer(customer)
                .build();

        TourEntity tourSaved = this.tourRepository.save(tourToSave);

        this.customerHelper.incrase(customer.getDni(), TourServiceImpl.class);
        return TourResponse.builder()
                .id(tourSaved.getId())
                .reservationIds(tourSaved.getReservations()
                        .stream()
                        .map(ReservationEntity::getId)
                        .collect(Collectors.toSet()))
                .ticketsIds(tourSaved.getTickets()
                        .stream()
                        .map(TicketEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public TourResponse read(Long id) {
        TourEntity tourFromDb = this.tourRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException(Tables.tour.name()));

        return TourResponse.builder()
                .reservationIds(tourFromDb.getReservations()
                        .stream()
                        .map(ReservationEntity::getId)
                        .collect(Collectors.toSet()))
                .ticketsIds(tourFromDb.getTickets()
                        .stream()
                        .map(TicketEntity::getId)
                        .collect(Collectors.toSet()))
                .id(tourFromDb.getId())
                .build();
    }

    @Override
    public void delete(Long id) {
        TourEntity tourToDelete = this.tourRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException(Tables.tour.name()));

        tourToDelete.getTickets().forEach(ticket -> ticket.setTour(null));
        tourToDelete.getReservations().forEach(reservation -> reservation.setTour(null));

        tourToDelete.getTickets().clear();
        tourToDelete.getReservations().clear();

        this.tourRepository.delete(tourToDelete);
    }

    @Override
    public void removeTicket(Long tourId, UUID ticketId) {
        TourEntity tourToUpdate = this.tourRepository.findById(tourId)
                .orElseThrow(() -> new IdNotFoundException(Tables.tour.name()));
        tourToUpdate.removeTicket(ticketId);
        this.tourRepository.save(tourToUpdate);
    }

    @Override
    public UUID addTicket(Long tourId, Long flyId) {
        TourEntity tourUpdate = this.tourRepository.findById(tourId)
                .orElseThrow(() -> new IdNotFoundException(Tables.tour.name()));
        FlyEntity fly = this.flyRepository.findById(flyId)
                .orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));
        TicketEntity ticket = this.tourHelper.createTicket(fly, tourUpdate.getCustomer());
        tourUpdate.addTicket(ticket);
        this.tourRepository.save(tourUpdate);
        return ticket.getId();
    }

    @Override
    public void removeReservation(Long tourId, UUID reservationId) {
        TourEntity tourUpdate = this.tourRepository.findById(tourId)
                .orElseThrow(() -> new IdNotFoundException(Tables.tour.name()));
        tourUpdate.removeReservation(reservationId);
        this.tourRepository.save(tourUpdate);
    }

    @Override
    public UUID addReservation(Long tourId, Long hotelId, Integer totalDays) {
        TourEntity tourUpdate = this.tourRepository.findById(tourId)
                .orElseThrow(() -> new IdNotFoundException(Tables.tour.name()));
        HotelEntity hotel = this.hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        ReservationEntity reservation = this.tourHelper.createReservation(hotel, tourUpdate.getCustomer(), totalDays);
        tourUpdate.addReservation(reservation);
        this.tourRepository.save(tourUpdate);
        return reservation.getId();
    }
}
