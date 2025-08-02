package com.alex.great_travel.api.controllers.status400;

import com.alex.great_travel.api.controllers.TicketController;
import com.alex.great_travel.api.models.request.TicketRequest;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.infrastructure.abstractService.TicketService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.Tables;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import com.alex.great_travel.util.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@Import(SecurityConfig.class)
class TicketControllerStatus400Test {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID INVALID_TICKET_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");
    private static final String RESOURCE_PATH = "/ticket";
    private static final String CUSTOMER = "CUSTOMER";


    @MockitoBean
    private TicketService ticketService;


    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should return 400 when request is invalid")
    void post_ShouldReturn400_WhenInvalidRequest() throws Exception {
        TicketRequest invalidRequest = new TicketRequest("489480", null, "email");

        mockMvc.perform(post(RESOURCE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName(" Shoud return 400 when ticket ID does not exist")
    @WithMockUser(roles = CUSTOMER)
    void get_ShouldReturn400_WhenIdNotFound() throws Exception {
        when(ticketService.read(INVALID_TICKET_ID)).thenThrow(new IdNotFoundException(Tables.ticket.name()));
        String uri = RESOURCE_PATH + "/" + INVALID_TICKET_ID;
        mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in ticket"));
    }

    @Test
    @DisplayName(" Shoud return 400 when fly ID does not exist")
    @WithMockUser(roles = CUSTOMER)
    void getFlyPrice_ShouldReturn400_WhenFlyIdNotFound() throws Exception {
        Long invalidFlyId = 0L;
        when(ticketService.findPrice(invalidFlyId)).thenThrow(new IdNotFoundException(Tables.fly.name()));
        String uri = RESOURCE_PATH;
        mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
                        .param("flyId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in fly"));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName(" Shoud return 400 when ticket ID does not exist")
    void put_ShouldReturn400_WhenIdNotFound() throws Exception {
        TicketRequest request = new TicketRequest();
        when(ticketService.update(request, INVALID_TICKET_ID)).thenThrow(new IdNotFoundException(Tables.ticket.name()));
        String uri = RESOURCE_PATH + "/" + INVALID_TICKET_ID;
        mockMvc.perform(put(uri).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should return 400 when request is invalid")
    void put_ShouldReturn400_WhenInvalidRequest() throws Exception {
        TicketRequest invalidRequest = new TicketRequest("489480", null, "email");

        String uri = RESOURCE_PATH + "/" + INVALID_TICKET_ID;
        mockMvc.perform(put(uri).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName(" Shoud return 400 when ticket ID does not exist")
    @WithMockUser(roles = CUSTOMER)
    void delete_ShouldReturn400_WhenIdNotFound() throws Exception {
        doThrow(new IdNotFoundException(Tables.ticket.name()))
                .when(ticketService).delete(INVALID_TICKET_ID);

        String uri = RESOURCE_PATH + "/" + INVALID_TICKET_ID;
        mockMvc.perform(delete(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in ticket"));
    }
}