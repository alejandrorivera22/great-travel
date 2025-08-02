package com.alex.great_travel.api.controllers.status200;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.controllers.TourController;
import com.alex.great_travel.api.models.request.TourFlyRequest;
import com.alex.great_travel.api.models.request.TourHotelRequest;
import com.alex.great_travel.api.models.request.TourRequest;
import com.alex.great_travel.api.models.response.TourResponse;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.domain.entities.CustomerEntity;
import com.alex.great_travel.domain.entities.RoleEntity;
import com.alex.great_travel.infrastructure.abstractService.TourService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.jwt.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TourController.class)
@Import(SecurityConfig.class)
class TourControllerStatus200Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TourService tourService;

    RoleEntity customerRole;
    CustomerEntity customer;
    TourRequest tourRequest;
    TourResponse tourResponse;
    Long tourId = 10L;
    UUID ticketId1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UUID reservationId1 = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static final String RESOURCE_PATH = "/tour";
    private static final String CUSTOMER = "CUSTOMER";

    @BeforeEach
    void setUp() {
        customerRole = DummyData.createRoleEntityCustomer();
        customer = DummyData.createCustomerEntity(customerRole);

        Set<TourFlyRequest> flightsRequest = Set.of(DummyData.createTourFlyRequest(1L));
        Set<TourHotelRequest> hotelsRequest = Set.of(DummyData.createTourHotelRequest(1L, 2));
        tourRequest = DummyData.createTourRequest(customer.getDni(), flightsRequest, hotelsRequest, customer.getEmail());

        tourResponse = DummyData.createTourResponse(
                tourId,
                Set.of(ticketId1),
                Set.of(reservationId1)
        );
    }

    @Test
    @DisplayName("Should create tour when request is valid")
    @WithMockUser(roles = CUSTOMER)
    void post_ShouldCreateTour() throws Exception {
        when(tourService.create(any(TourRequest.class))).thenReturn(tourResponse);

        mockMvc.perform(post(RESOURCE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tourRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(tourResponse.getId()))
                .andExpect(jsonPath("$.ticketsIds", hasItem(ticketId1.toString())))
                .andExpect(jsonPath("$.reservationIds", hasItem(reservationId1.toString())));
    }

    @Test
    @DisplayName("Should return tour when it exists")
    @WithMockUser(roles = CUSTOMER)
    void get_ShouldReturnTour() throws Exception {
        when(tourService.read(tourId)).thenReturn(tourResponse);

        String uri = RESOURCE_PATH + "/" + tourId;
        mockMvc.perform(get(uri)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tourResponse.getId()))
                .andExpect(jsonPath("$.ticketsIds", hasItem(ticketId1.toString())))
                .andExpect(jsonPath("$.reservationIds", hasItem(reservationId1.toString())));


    }

    @Test
    @DisplayName("Should remove ticket and return no content")
    @WithMockUser(roles = CUSTOMER)
    void delete_ShouldReturnNoContent() throws Exception {
        String uri = RESOURCE_PATH + "/" + tourId;

        mockMvc.perform(delete(uri))
                .andExpect(status().isNoContent());

        verify(tourService, times(1)).delete(eq(tourId));
    }

    @Test
    @DisplayName("Should remove ticket and return no content")
    @WithMockUser(roles = CUSTOMER)
    void deleteTicket_ShouldReturnNoContent() throws Exception {
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        String uri = String.format("%s/%d/remove_ticket/%s", RESOURCE_PATH, tourId, ticketId);

        mockMvc.perform(patch(uri))
                .andExpect(status().isNoContent());

        verify(tourService, times(1)).removeTicket(eq(tourId), eq(ticketId));
    }


    @Test
    @DisplayName("Should remove reservation and return no content")
    @WithMockUser(roles = CUSTOMER)
    void deleteReservation_ShouldReturnReservationId() throws Exception {
        String uri = String.format("%s/%d/remove_reservation/%s", RESOURCE_PATH, tourId, reservationId1);

        mockMvc.perform(patch(uri))
                .andExpect(status().isNoContent());

        verify(tourService, times(1)).removeReservation(eq(tourId), eq(reservationId1));
    }

    @Test
    @DisplayName("Should add reservation and return reservation id (happy path)")
    @WithMockUser(roles = CUSTOMER)
    void postReservation_ShouldReturnReservationId() throws Exception {
        Integer totalDays = 3;
        Long hotelId = 5L;
        UUID expectedReservationId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        String uri = String.format("%s/%d/add_reservation/%d", RESOURCE_PATH, tourId, hotelId);

        when(tourService.addReservation(eq(tourId), eq(hotelId), eq(totalDays)))
                .thenReturn(expectedReservationId);

        mockMvc.perform(patch(uri)
                        .param("totalDays", totalDays.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ReservationId").value(expectedReservationId.toString()));
    }
}