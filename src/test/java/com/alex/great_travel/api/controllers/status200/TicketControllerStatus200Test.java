package com.alex.great_travel.api.controllers.status200;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.controllers.TicketController;
import com.alex.great_travel.api.models.request.TicketRequest;
import com.alex.great_travel.api.models.response.FlyResponse;
import com.alex.great_travel.api.models.response.TicketResponse;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.domain.entities.CustomerEntity;
import com.alex.great_travel.domain.entities.RoleEntity;
import com.alex.great_travel.infrastructure.abstractService.TicketService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.AeroLine;
import com.alex.great_travel.util.TravelUtil;
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
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@Import(SecurityConfig.class)
class TicketControllerStatus200Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String RESOURCE_PATH = "/ticket";
    private static final String CUSTOMER = "CUSTOMER";

    @MockitoBean
    private TicketService ticketService;

    public static final BigDecimal CHARGER_PRICE_PERCENTAGE = BigDecimal.valueOf(1.25);
    private static final UUID TICKET_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");
    private static final String CLIENT_ID = "VIKI771012HMCRG093";
    private static final Long FLY_ID = 1L;

    RoleEntity role;
    CustomerEntity customer;
    FlyResponse fly;
    TicketRequest ticketRequest;
    TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        role = DummyData.createRoleEntityCustomer();
        customer = DummyData.createCustomerEntity(role);
        fly = DummyData.createFlyResponse(
                99.9999,
                88.8888,
                11.1111,
                22.2222,
                "Mexico",
                "Grecia",
                AeroLine.aero_gold.name(),
                new BigDecimal("45.0")
        );
        ticketRequest = DummyData.createTicketRequest(CLIENT_ID, FLY_ID, customer.getEmail());
        ticketResponse = TicketResponse.builder()
                .id(TICKET_ID)
                .purchaseDate(LocalDate.now())
                .arrivalDate(TravelUtil.getRandomLatter())
                .departureDate(TravelUtil.getRandomSoon())
                .price(fly.getPrice().multiply(CHARGER_PRICE_PERCENTAGE))
                .fly(fly)
                .build();


        when(ticketService.read(TICKET_ID)).thenReturn(ticketResponse);
    }

    @Test
    @DisplayName("Shoul create ticket when request is valid")
    @WithMockUser(roles = CUSTOMER)
    void post_ShouldReturnTicketResponse() throws Exception {
        when(ticketService.create(any(TicketRequest.class))).thenReturn(ticketResponse);
        mockMvc.perform(post(RESOURCE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ticketResponse.getId().toString()))
                .andExpect(jsonPath("$.price").value(ticketResponse.getPrice().doubleValue()))
                .andExpect(jsonPath("$.fly.id").value(ticketResponse.getFly().getId()));
    }

    @Test
    @DisplayName("Should return ticket when it exists")
    @WithMockUser(roles = CUSTOMER)
    void get_ShouldReturnTicket_WhenExists() throws Exception {
        String uri = RESOURCE_PATH + "/" + TICKET_ID;
        mockMvc.perform(get(uri)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketResponse.getId().toString()))
                .andExpect(jsonPath("$.price").value(ticketResponse.getPrice().doubleValue()))
                .andExpect(jsonPath("$.fly.id").value(ticketResponse.getFly().getId()));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    void getFlyPrice() throws Exception {
        BigDecimal expectedPrice = fly.getPrice().multiply(CHARGER_PRICE_PERCENTAGE);
        when(ticketService.findPrice(FLY_ID)).thenReturn(expectedPrice);
        mockMvc.perform(get(RESOURCE_PATH)
                        .param("flyId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flyprice").value(ticketResponse.getPrice().doubleValue()));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should update ticket given a valid ID and request")
    void put_ShouldReturnUpdatedTicket() throws Exception {
        String uri = RESOURCE_PATH + "/" + TICKET_ID;
        fly.setId(2L);
        ticketRequest.setFlyId(2L);
        ticketResponse.setFly(fly);

        when(ticketService.update(eq(ticketRequest), eq(TICKET_ID)))
                .thenReturn(ticketResponse);

        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketResponse.getId().toString()))
                .andExpect(jsonPath("$.fly.id").value(ticketResponse.getFly().getId()));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should delete ticket when ID exists")
    void delete_ShouldReturnNoContent_WhenTicketExists() throws Exception {
        String uri = RESOURCE_PATH + "/" + TICKET_ID;
        mockMvc.perform(delete(uri))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).delete(eq(TICKET_ID));
    }
}