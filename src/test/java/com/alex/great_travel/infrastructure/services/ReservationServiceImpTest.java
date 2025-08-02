package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.models.request.ReservationRequest;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.api.models.response.ReservationResponse;
import com.alex.great_travel.domain.entities.*;
import com.alex.great_travel.domain.repositories.CustomerRepository;
import com.alex.great_travel.domain.repositories.HotelRepository;
import com.alex.great_travel.domain.repositories.ReservationRepository;
import com.alex.great_travel.infrastructure.helpers.CustomerHelper;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceImpTest extends ServiceSpec{

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private CustomerHelper customerHelper;

    @InjectMocks
    private ReservationServiceImp reservationService;

    private static final BigDecimal CHARGER_PRICE_PERCENTAGE = BigDecimal.valueOf(1.25);

    HotelEntity hotel;
    CustomerEntity customer;
    RoleEntity role;
    ReservationRequest reservationRequest;
    ReservationEntity reservation;
    String customerDni;
    Long hotelId;
    UUID reservationId;

    @BeforeEach
    void setUp() {
        hotel = DummyData.createHotelEntity();
        role = DummyData.createRoleEntityCustomer();
        customer = DummyData.createCustomerEntity(role);
        reservationRequest = DummyData.createReservationRequest(customer.getDni(), hotel.getId(), 3, customer.getEmail());
        reservation = DummyData.createReservationEntity(customer, hotel, 3, CHARGER_PRICE_PERCENTAGE);
        customerDni = customer.getDni();
        hotelId = hotel.getId();
        reservationId = reservation.getId();
    }

    @Test
    @DisplayName("should create a reservation and return a response")
    void create_ShouldSaveReservationAndReturnResponse() {
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(customerRepository.findById(customerDni)).thenReturn(Optional.of(customer));
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

        ReservationResponse response = reservationService.create(reservationRequest);

        assertNotNull(response);
        assertEquals(reservationRequest.getHotelId(), response.getHotel().getId());
        assertEquals(reservationRequest.getTotalDays(), response.getTotalDays());
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));

    }

    @Test
    @DisplayName("happy path should return reservation when it exists")
    void read_ShouldReturnReservation_WhenExists() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationService.read(reservationId);
        assertNotNull(response);

        assertAll(
                () -> assertEquals(reservationId, response.getId()),
                () -> assertEquals(hotelId, response.getHotel().getId()),
                () -> assertEquals(reservation.getPrice(), response.getPrice())
        );
    }


    @Test
    @DisplayName("Unhappy path Should throw IdNotFoundException when reservation does not exist")
    void read_ShouldThrowException_WhenReservationIdNotFound() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> reservationService.read(reservationId));
    }

    @Test
    @DisplayName("Happy payh should return hotel price given a vaid hotel ID")
    void findPrice_ShouldReturnHotelPrice_GivenHotelId() {
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));

        BigDecimal response = reservationService.findPrice(hotelId);
        assertNotNull(response);

        BigDecimal priceExpected = hotel.getPrice().multiply(CHARGER_PRICE_PERCENTAGE);

       assertEquals(priceExpected, response);
    }

    @Test
    @DisplayName("Happy path Should update a reservation given a valid request and ID")
    void update_ShouldReturnUpdateReservation() {
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationService.read(reservationId);
        assertNotNull(response);

        assertAll(
                () -> assertEquals(reservationId, response.getId()),
                () -> assertEquals(hotelId, response.getHotel().getId()),
                () -> assertEquals(reservation.getPrice(), response.getPrice()),
                () -> assertEquals(reservation.getTotalDays(), response.getTotalDays())
        );

        hotel.setId(2L);
        when(hotelRepository.findById(2L)).thenReturn(Optional.of(hotel));
        when(reservationRepository.save(any(ReservationEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationRequest request = DummyData.createReservationRequest(customerDni, 2L, 5, customer.getEmail());
        ReservationResponse updateResponse = reservationService.update(request, reservationId);

        assertAll(
                () -> assertEquals(reservationId, updateResponse.getId()),
                () -> assertEquals(2L, updateResponse.getHotel().getId()),
                () -> assertEquals(request.getTotalDays(), updateResponse.getTotalDays())
        );

    }

    @Test
    @DisplayName("Unhappy path Should throw IdNotFoundException when hotel Id does not exists")
    void update_ShouldThrowException_WhenHotelIdNotFound() {
        Long invalidID = 0L;
        when(hotelRepository.findById(invalidID)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> reservationService.update(new ReservationRequest(), reservationId));
        verify(reservationRepository, never()).save(any(ReservationEntity.class));
    }

    @Test
    @DisplayName("Unhappy path Should throw IdNotFoundException when hotel Id does not exists")
    void update_ShouldThrowException_WhenReservationIdNotFound() {
        UUID invalidID = UUID.randomUUID();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(reservationRepository.findById(invalidID)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> reservationService.update(new ReservationRequest(), invalidID));
        verify(reservationRepository, never()).save(any(ReservationEntity.class));
    }

    @Test
    @DisplayName("Should delete reservation given valid UUID")
    void delete() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        reservationService.delete(reservationId);
        verify(reservationRepository, atLeastOnce()).delete(any(ReservationEntity.class));
    }
}