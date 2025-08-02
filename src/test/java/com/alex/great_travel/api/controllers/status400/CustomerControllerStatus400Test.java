package com.alex.great_travel.api.controllers.status400;

import com.alex.great_travel.api.controllers.CustomerController;
import com.alex.great_travel.api.models.request.CustomerRequest;
import com.alex.great_travel.api.models.request.CustomerUpdateRequest;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.infrastructure.abstractService.CustomerService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.Role;
import com.alex.great_travel.util.Tables;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerControllerStatus400Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INVALID_CUSTOMER_ID = "Invalid";
    private static final String RESOURCE_PATH = "/customer";
    private static final String ADMIN = "ADMIN";
    private static final String CUSTOMER = "CUSTOMER";

    @MockitoBean
    private CustomerService customerService;


    @Test
    @DisplayName(" Shoud return 400 when customer ID does not exist")
    @WithMockUser(roles = ADMIN)
    void getByDni_ShouldReturn400_WhenIdNotFound() throws Exception {
        when(customerService.read(INVALID_CUSTOMER_ID)).thenThrow(new IdNotFoundException(Tables.customer.name()));
        String uri = RESOURCE_PATH + "/" + INVALID_CUSTOMER_ID;
        mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in customer"));
    }

    @Test
    @WithMockUser(roles = ADMIN)
    @DisplayName("Should return 400 when request is invalid")
    void put_ShouldReturn400_WhenInvalidRequest() throws Exception{
        CustomerUpdateRequest invalidRequest = new CustomerUpdateRequest("00", "55");

        mockMvc.perform(put(RESOURCE_PATH + "/" + INVALID_CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName(" Shoud return 400 when customer ID does not exist")
    void updateUsername_ShouldReturn400_WhenIdNotFound() throws Exception {
        when(customerService.updateUsername(INVALID_CUSTOMER_ID, "newusername")).thenThrow(new IdNotFoundException(Tables.customer.name()));
        String uri = RESOURCE_PATH + "/" + INVALID_CUSTOMER_ID + "/username";
        mockMvc.perform(patch(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "newusername"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in customer"));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName(" Shoud return 400 when customer ID does not exist")
    void updatePasword_ShouldReturn400_WhenIdNotFound() throws Exception {
        when(customerService.updatePassword(INVALID_CUSTOMER_ID, "newPassword"))
                .thenThrow(new IdNotFoundException(Tables.customer.name()));
        String uri = RESOURCE_PATH + "/" + INVALID_CUSTOMER_ID + "/password";
        mockMvc.perform(patch(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("password", "newPassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in customer"));
    }

    @Test
    @WithMockUser(roles = ADMIN)
    @DisplayName(" Shoud return 400 when customer ID does not exist")
    void addRole_ShouldReturn400_WhenUsernameNotFound() throws Exception {
        when(customerService.addRole("username", Role.ADMIN))
                .thenThrow(new IdNotFoundException(Tables.customer.name()));
        String uri = RESOURCE_PATH + "/add-role";
        mockMvc.perform(patch(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "username")
                        .param("role", "ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Record no exist in customer"));
    }

}