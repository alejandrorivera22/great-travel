package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.models.request.TourFlyRequest;
import com.alex.great_travel.api.models.request.TourHotelRequest;
import com.alex.great_travel.api.models.request.TourRequest;
import com.alex.great_travel.api.models.response.TourResponse;
import com.alex.great_travel.domain.entities.*;
import com.alex.great_travel.domain.repositories.CustomerRepository;
import com.alex.great_travel.domain.repositories.FlyRepository;
import com.alex.great_travel.domain.repositories.HotelRepository;
import com.alex.great_travel.domain.repositories.TourRepository;
import com.alex.great_travel.infrastructure.helpers.CustomerHelper;
import com.alex.great_travel.infrastructure.helpers.TourHelper;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TourServiceImplTest extends ServiceSpec{

    @Mock
    private TourRepository tourRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private FlyRepository flyRepository;
    @Mock
    private TourHelper tourHelper;
    @Mock
    private CustomerHelper customerHelper;

    @InjectMocks
    TourServiceImpl tourService;

    CustomerEntity customer;
    RoleEntity customerRole;
    FlyEntity fly1;
    FlyEntity fly2;
    HotelEntity hotel1;
    HotelEntity hotel2;
    TourRequest request;
    TourEntity savedTour;
    UUID ticketId1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UUID reservationId1 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    Long tourId = 10L;

    @BeforeEach
    void setUp() {
        customerRole = DummyData.createRoleEntityCustomer();
        customer = DummyData.createCustomerEntity(customerRole);

        fly1 = DummyData.createFlyEntity(10.0, 20.0, 30.0, 40.0, "Orig", "Dest", "aero_gold", new BigDecimal("50.00"));
        fly2 = DummyData.createFlyEntity(11.0, 21.0, 31.0, 41.0, "Orig2", "Dest2", "aero_gold", new BigDecimal("60.00"));

        // Asignar IDs explícitos para evitar null y duplicados en Set.of
        fly1.setId(1L);
        fly2.setId(2L);

        hotel1 = DummyData.createHotelEntity();
        hotel2 = HotelEntity.builder()
                .id(2L)
                .price(BigDecimal.valueOf(200))
                .name("Hotel2")
                .rating(4)
                .address("address hotel 2")
                .reservation(Set.of(new ReservationEntity()))
                .build();

        Set<TourFlyRequest> flightsReq = Set.of(
                DummyData.createTourFlyRequest(fly1.getId()),
                DummyData.createTourFlyRequest(fly2.getId())
        );
        Set<TourHotelRequest> hotelsReq = Set.of(
                DummyData.createTourHotelRequest(hotel1.getId(), 2),
                DummyData.createTourHotelRequest(hotel2.getId(), 3)
        );
        request = DummyData.createTourRequest(customer.getDni(), flightsReq, hotelsReq, customer.getEmail());

        // Construcción del tour esperado...
        TicketEntity ticket = TicketEntity.builder()
                .id(ticketId1)
                .customer(customer)
                .fly(fly1)
                .tour(null)
                .price(fly1.getPrice().multiply(BigDecimal.valueOf(1.25)))
                .departureDate(LocalDateTime.now())
                .arrivalDate(LocalDateTime.now().plusHours(5))
                .purchaseDate(LocalDate.now())
                .build();

        ReservationEntity reservation = ReservationEntity.builder()
                .id(reservationId1)
                .customer(customer)
                .hotel(hotel1)
                .totalDays(2)
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(2))
                .dateTimeReservation(LocalDateTime.now())
                .price(hotel1.getPrice().multiply(BigDecimal.valueOf(1.25)))
                .build();

        savedTour = TourEntity.builder()
                .id(tourId)
                .customer(customer)
                .tickets(new HashSet<>(Set.of(ticket)))
                .reservations(new HashSet<>(Set.of(reservation)))
                .build();

        ticket.setTour(savedTour);
        reservation.setTour(savedTour);
    }

    @Test
    @DisplayName("should create a tour and return response with ticket and reservation ids")
    void create_ShouldSaveTourAndReturnResponse() {
        when(customerRepository.findById(customer.getDni())).thenReturn(Optional.of(customer));
        when(flyRepository.findById(fly1.getId())).thenReturn(Optional.of(fly1));
        when(flyRepository.findById(fly2.getId())).thenReturn(Optional.of(fly2));
        when(hotelRepository.findById(hotel1.getId())).thenReturn(Optional.of(hotel1));
        when(hotelRepository.findById(hotel2.getId())).thenReturn(Optional.of(hotel2));

        when(tourHelper.createTickets(anySet(), eq(customer)))
                .thenReturn(savedTour.getTickets());
        when(tourHelper.createReservations((HashMap<HotelEntity, Integer>) any(), eq(customer)))
                .thenReturn(savedTour.getReservations());

        when(tourRepository.save(any(TourEntity.class))).thenReturn(savedTour);

        TourResponse response = tourService.create(request);

        assertNotNull(response);
        assertEquals(tourId, response.getId());
        assertTrue(response.getTicketsIds().contains(ticketId1));
        assertTrue(response.getReservationIds().contains(reservationId1));
        verify(tourRepository, times(1)).save(any(TourEntity.class));
        verify(customerHelper, times(1)).incrase(customer.getDni(), TourServiceImpl.class);
    }

    @Test
    @DisplayName("Unhappy path: should throw when customer does not exist")
    void create_ShouldThrow_WhenCustomerNotFound() {
        when(customerRepository.findById(customer.getDni())).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> tourService.create(request));
        verify(tourRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unhappy path: should throw when a fly in request does not exist")
    void create_ShouldThrow_WhenFlyNotFound() {
        when(customerRepository.findById(customer.getDni())).thenReturn(Optional.of(customer));
        when(flyRepository.findById(fly1.getId())).thenReturn(Optional.empty()); // uno falla

        assertThrows(IdNotFoundException.class, () -> tourService.create(request));
        verify(tourRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unhappy path: should throw when a hotel in request does not exist")
    void create_ShouldThrow_WhenHotelNotFound() {
        when(customerRepository.findById(customer.getDni())).thenReturn(Optional.of(customer));
        when(flyRepository.findById(fly1.getId())).thenReturn(Optional.of(fly1));
        when(flyRepository.findById(fly2.getId())).thenReturn(Optional.of(fly2));
        when(hotelRepository.findById(hotel1.getId())).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> tourService.create(request));
        verify(tourRepository, never()).save(any());
    }

    @Test
    @DisplayName("happy path should return tour when it exists")
    void read_ShouldReturnTour_WhenExists() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(savedTour));

        TourResponse response = tourService.read(tourId);
        assertNotNull(response);
        assertEquals(tourId, response.getId());
        assertEquals(savedTour.getTickets().stream().map(TicketEntity::getId).collect(Collectors.toSet()), response.getTicketsIds());
        assertEquals(savedTour.getReservations().stream().map(ReservationEntity::getId).collect(Collectors.toSet()), response.getReservationIds());
    }

    @Test
    @DisplayName("Unhappy path: should throw when tour does not exist")
    void read_ShouldThrow_WhenNotFound() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> tourService.read(tourId));
    }

    @Test
    @DisplayName("should delete tour and clear associations before delete")
    void delete_ShouldRemoveTour() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(savedTour));

        tourService.delete(tourId);

        verify(tourRepository).delete(savedTour);
    }

    @Test
    @DisplayName("Unhappy path: removeTicket should throw if tour not found")
    void removeTicket_ShouldThrow_WhenTourNotFound() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> tourService.removeTicket(tourId, ticketId1));
    }

    @Test
    @DisplayName("Unhappy path: addTicket should throw if fly not found")
    void addTicket_ShouldThrow_WhenFlyNotFound() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(savedTour));
        when(flyRepository.findById(fly1.getId())).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> tourService.addTicket(tourId, fly1.getId()));
    }

    @Test
    @DisplayName("happy path addTicket returns new ticket id")
    void addTicket_ShouldReturnNewTicketId() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(savedTour));
        when(flyRepository.findById(fly1.getId())).thenReturn(Optional.of(fly1));

        TicketEntity newTicket = TicketEntity.builder()
                .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .customer(customer)
                .fly(fly1)
                .price(fly1.getPrice().multiply(BigDecimal.valueOf(1.25)))
                .departureDate(LocalDateTime.now())
                .arrivalDate(LocalDateTime.now().plusHours(5))
                .purchaseDate(LocalDate.now())
                .build();

        when(tourHelper.createTicket(fly1, customer)).thenReturn(newTicket);

        UUID returned = tourService.addTicket(tourId, fly1.getId());
        assertEquals(newTicket.getId(), returned);
        verify(tourRepository).save(any());
    }

}