package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.models.request.ReservationRequest;
import com.alex.great_travel.api.models.request.TicketRequest;
import com.alex.great_travel.api.models.response.ReservationResponse;
import com.alex.great_travel.api.models.response.TicketResponse;
import com.alex.great_travel.domain.entities.*;
import com.alex.great_travel.domain.repositories.*;
import com.alex.great_travel.infrastructure.helpers.CustomerHelper;
import com.alex.great_travel.util.AeroLine;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest extends ServiceSpec {

    @Mock
    private FlyRepository flyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private CustomerHelper customerHelper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    public static final BigDecimal CHARGER_PRICE_PERCENTAGE = BigDecimal.valueOf(1.25);

    FlyEntity fly;
    RoleEntity role;
    CustomerEntity customer;
    TicketEntity ticket;
    UUID ticketId;
    TicketRequest request;
    TourEntity tour;
    Long flyId;
    String customerDni;

    @BeforeEach
    void setUp() {
        fly =  DummyData.createFlyEntity(
                99.9999,
                88.8888,
                11.1111,
                22.2222,
                "Mexico",
                "Grecia",
                AeroLine.aero_gold.name(),
                new BigDecimal("43.00"
                ));
        flyId = fly.getId();
        role = DummyData.createRoleEntityAdmin();
        customer = DummyData.createCustomerEntity(role);
        customerDni = customer.getDni();
        tour = new TourEntity();
        ticket = DummyData.createTicketEntity(fly, tour, CHARGER_PRICE_PERCENTAGE, customer);
        ticketId = ticket.getId();
        request = DummyData.createTicketRequest(customerDni, flyId, customer.getEmail());
    }

    @Test
    @DisplayName("should create a ticket and return a response")
    void create_ShouldSaveTicketAndReturnResponse() {
        when(flyRepository.findById(flyId)).thenReturn(Optional.of(fly));
        when(customerRepository.findById(customerDni)).thenReturn(Optional.of(customer));
        when(ticketRepository.save(any(TicketEntity.class))).thenReturn(ticket);

        TicketResponse response = ticketService.create(request);

        assertNotNull(response);
        assertEquals(request.getFlyId(), response.getFly().getId());
        verify(ticketRepository, times(1)).save(any(TicketEntity.class));
    }

    @Test
    @DisplayName("happy path should return ticket when it exists")
    void read_ShouldReturnTicket_WhenExists() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        TicketResponse response = ticketService.read(ticketId);
        assertNotNull(response);

        BigDecimal expectedPrice = fly.getPrice().multiply(CHARGER_PRICE_PERCENTAGE);
        assertAll(
                () -> assertEquals(ticketId, response.getId()),
                () -> assertEquals(flyId, response.getFly().getId()),
                () -> assertEquals(expectedPrice, response.getPrice())
        );
    }


    @Test
    @DisplayName("Unhappy path Should throw IdNotFoundException when ticket does not exist")
    void read_ShouldThrowException_WhenTicketIdNotFound() {
        UUID invaidId = UUID.randomUUID();
        when(ticketRepository.findById(invaidId)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> ticketService.read(invaidId));
    }

    @Test
    @DisplayName("Happy payh shoud return fly price given a vaid fly ID")
    void findPrice_ShouldReturnFlyPrice_GivenFlyId() {
        when(flyRepository.findById(flyId)).thenReturn(Optional.of(fly));

        BigDecimal response = ticketService.findPrice(flyId);
        assertNotNull(response);

        BigDecimal priceExpected = fly.getPrice().multiply(CHARGER_PRICE_PERCENTAGE);

        assertEquals(priceExpected, response);
    }

    @Test
    @DisplayName("Happy path Should update a ticket given a valid request and ticket ID")
    void update_ShouldReturnUpdateTicket() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(flyRepository.findById(flyId)).thenReturn(Optional.of(fly));

        TicketResponse response = ticketService.read(ticketId);
        assertNotNull(response);

        assertAll(
                () -> assertEquals(ticketId, response.getId()),
                () -> assertEquals(flyId, response.getFly().getId()),
                () -> assertEquals(ticket.getPrice(), response.getPrice())
        );

        fly.setId(2L);
        when(flyRepository.findById(2L)).thenReturn(Optional.of(fly));
        when(ticketRepository.save(any(TicketEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TicketRequest request = DummyData.createTicketRequest(customerDni, 2L, customer.getEmail());
        TicketResponse updateResponse = ticketService.update(request, ticketId);

        assertAll(
                () -> assertEquals(response.getId(), updateResponse.getId()),
                () -> assertEquals(2L, updateResponse.getFly().getId())
        );
    }

    @Test
    @DisplayName("Unhappy path Should throw IdNotFoundException when ticket Id does not exists")
    void update_ShouldThrowException_WhenTicketIdNotFound() {
        UUID invalidId = UUID.randomUUID();
        when(ticketRepository.findById(invalidId)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> ticketService.update(new TicketRequest(), invalidId));
        verify(ticketRepository, never()).save(any(TicketEntity.class));
    }

    @Test
    @DisplayName("Unhappy path Should throw IdNotFoundException when fly Id does not exists")
    void update_ShouldThrowException_WhenFlyIdNotFound() {
        Long invalidId = 0L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(flyRepository.findById(invalidId)).thenReturn(Optional.empty());
        assertThrows(IdNotFoundException.class, () -> ticketService.update(new TicketRequest(), ticketId));
        verify(ticketRepository, never()).save(any(TicketEntity.class));
    }

    @Test
    @DisplayName("Should delete ticked given valid id")
    void delete() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        ticketRepository.delete(ticket);
        verify(ticketRepository, atLeastOnce()).delete(any(TicketEntity.class));
    }
}