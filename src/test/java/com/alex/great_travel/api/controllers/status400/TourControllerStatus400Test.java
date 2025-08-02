package com.alex.great_travel.api.controllers.status400;

import com.alex.great_travel.api.controllers.TourController;
import com.alex.great_travel.api.models.request.TourRequest;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.infrastructure.abstractService.TourService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TourController.class)
@Import(SecurityConfig.class)
class TourControllerStatus400Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long INVALID_TOUR_ID = 0L;
    private static final String RESOURCE_PATH = "/tour";
    private static final String CUSTOMER = "CUSTOMER";

    @MockitoBean
    private TourService tourService;

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should return 400 when request is invalid")
    void post_ShouldReturn400_WhenInvalidRequest() throws Exception{
        TourRequest invalidRequest = new TourRequest();

        mockMvc.perform(post(RESOURCE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName(" Shoud return 400 when tour ID does not exist")
    @WithMockUser(roles = CUSTOMER)
    void get_ShouldReturn400_WhenIdNotFound() throws Exception {
        when(tourService.read(INVALID_TOUR_ID)).thenThrow(new IdNotFoundException(Tables.tour.name()));
        String uri = RESOURCE_PATH + "/" + INVALID_TOUR_ID;
        mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in tour"));
    }

    @Test
    @DisplayName(" Shoud return 400 when tour ID does not exist")
    @WithMockUser(roles = CUSTOMER)
    void delete_ShouldReturn400_WhenIdNotFound() throws Exception {
        doThrow(new IdNotFoundException(Tables.ticket.name()))
                .when(tourService).delete(INVALID_TOUR_ID);

        String uri = RESOURCE_PATH + "/" + INVALID_TOUR_ID;
        mockMvc.perform(delete(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in ticket"));
    }

    @Test
    @DisplayName(" Shoud return 400 when tour ID does not exist")
    @WithMockUser(roles = CUSTOMER)
    void deleteTicket() throws Exception {
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        String uri = String.format("%s/%d/remove_ticket/%s", RESOURCE_PATH, INVALID_TOUR_ID, ticketId);

        doThrow(new IdNotFoundException(Tables.tour.name()))
                .when(tourService).removeTicket(INVALID_TOUR_ID, ticketId);

        mockMvc.perform(patch(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in tour"));

        verify(tourService, times(1)).removeTicket(eq(INVALID_TOUR_ID), eq(ticketId));

    }

}