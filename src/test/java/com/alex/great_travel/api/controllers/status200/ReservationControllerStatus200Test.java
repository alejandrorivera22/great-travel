package com.alex.great_travel.api.controllers.status200;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.controllers.ReservationController;
import com.alex.great_travel.api.models.request.ReservationRequest;
import com.alex.great_travel.api.models.response.HotelResponse;
import com.alex.great_travel.api.models.response.ReservationResponse;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.domain.entities.CustomerEntity;
import com.alex.great_travel.domain.entities.HotelEntity;
import com.alex.great_travel.domain.entities.RoleEntity;
import com.alex.great_travel.infrastructure.abstractService.ReservationService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.jwt.JwtUtils;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfig.class)
class ReservationControllerStatus200Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String RESOURCE_PATH = "/reservation";
    private static final String CUSTOMER = "CUSTOMER";

    @MockitoBean
    private ReservationService reservationService;

    RoleEntity role;
    HotelEntity hotel;
    CustomerEntity customer;
    ReservationRequest request;
    ReservationResponse reservationResponse;
    HotelResponse hotelResponse;

    public static final Long HOTEL_ID = 1L;
    public static final UUID RESERVATION_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");
    public static final BigDecimal CHARGER_PRICE_PERCENTAGE = BigDecimal.valueOf(1.25);


    @BeforeEach
    void setUp() {
        role = DummyData.createRoleEntityAdmin();
        customer = DummyData.createCustomerEntity(role);
        hotel = DummyData.createHotelEntity();
        hotelResponse = HotelResponse.builder()
                .name("Hotel1")
                .build();
        request = ReservationRequest.builder()
                .clientId(customer.getDni())
                .email(customer.getEmail())
                .hotelId(1L)
                .totalDays(3)
                .build();
        reservationResponse = ReservationResponse.builder()
                .id(RESERVATION_ID)
                .price(hotel.getPrice().multiply(CHARGER_PRICE_PERCENTAGE))
                .hotel(hotelResponse)
                .build();

        when(reservationService.read(RESERVATION_ID)).thenReturn(reservationResponse);
    }

    @Test
    @DisplayName("Shoul create reservation when request is valid")
    @WithMockUser(roles = CUSTOMER)
    void post_ShouldReturnReservationResponse() throws Exception {
        when(reservationService.create(any(ReservationRequest.class))).thenReturn(reservationResponse);
        mockMvc.perform(post(RESOURCE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reservationResponse.getId().toString()))
                .andExpect(jsonPath("$.hotel.name").value(reservationResponse.getHotel().getName()))
                .andExpect(jsonPath("$.totalDays").value(reservationResponse.getTotalDays()));
    }

    @Test
    @DisplayName("Should return reservation when it exists")
    @WithMockUser(roles = CUSTOMER)
    void get_ShouldReturnReservation_WhenExists() throws Exception {
        String uri = RESOURCE_PATH + "/" + RESERVATION_ID;
        mockMvc.perform(get(uri)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationResponse.getId().toString()))
                .andExpect(jsonPath("$.price").value(reservationResponse.getPrice().doubleValue()))
                .andExpect(jsonPath("$.totalDays").value(reservationResponse.getTotalDays()));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    void getHotelPrice() throws Exception {
        BigDecimal expectedPrice = hotel.getPrice().multiply(CHARGER_PRICE_PERCENTAGE);
        when(reservationService.findPrice(HOTEL_ID)).thenReturn(expectedPrice);
        mockMvc.perform(get(RESOURCE_PATH)
                        .param("hotelId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelprice").value(reservationResponse.getPrice().doubleValue()));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should update reservation given a valid ID and request")
    void put_ShouldReturnUpdatedReservation() throws Exception {
        String uri = RESOURCE_PATH + "/" + RESERVATION_ID;
        request.setTotalDays(5);
        reservationResponse.setTotalDays(5);

        when(reservationService.update(eq(request), eq(RESERVATION_ID)))
                .thenReturn(reservationResponse);

        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationResponse.getId().toString()))
                .andExpect(jsonPath("$.totalDays").value(5));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should delete reservation when ID exists")
    void delete_ShouldReturnNoContent_WhenReservationExists() throws Exception {
        String uri = RESOURCE_PATH + "/" + RESERVATION_ID;
        mockMvc.perform(delete(uri))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).delete(eq(RESERVATION_ID));
    }
}