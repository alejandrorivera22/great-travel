package com.alex.great_travel.api.controllers.status200;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.controllers.CustomerController;
import com.alex.great_travel.api.models.request.CustomerRequest;
import com.alex.great_travel.api.models.request.CustomerUpdateRequest;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.infrastructure.abstractService.CustomerService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.Role;
import com.alex.great_travel.util.jwt.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.C;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerControllerStatus200Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String RESOURCE_PATH = "/customer";
    private static final String ADMIN = "ADMIN";
    private static final String CUSTOMER = "CUSTOMER";

    @MockitoBean
    private CustomerService customerService;


    CustomerResponse customerResponse;
    CustomerRequest customerRequest;
    final static private String CUSTOMER_ID = "CustomerDni";

    @BeforeEach
    void setUp() {
        customerResponse = DummyData.createCustomerResponse();
        customerResponse.getRoles().add(CUSTOMER);
        customerRequest = DummyData.createCustomerRequest();
        when(customerService.read(CUSTOMER_ID)).thenReturn(customerResponse);
    }

    @Test
    @DisplayName("Should return customer when it exists")
    @WithMockUser(roles = ADMIN)
    void getByDni_ShouldReturnCustomer() throws Exception {
        String uri = RESOURCE_PATH + "/" + CUSTOMER_ID;
        mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(customerResponse.getUsername()))
                .andExpect(jsonPath("$.email").value(customerResponse.getEmail()))
                .andExpect(jsonPath("$.roles[0]").value(CUSTOMER));
    }

    @Test
    @WithMockUser(roles = ADMIN)
    @DisplayName("Should update customer given a valid ID and request")
    void put_ShouldReturnUpdatedCustomer_WhenValidRequestAndId() throws Exception {
        String uri = RESOURCE_PATH + "/" + CUSTOMER_ID;
        CustomerUpdateRequest customerRequestUpdate =  CustomerUpdateRequest.builder()
                .creditCard("2727-2828-2929-3030")
                .phoneNumber("33-44-55-66")
                .build();
        CustomerResponse updatedResponse = DummyData.createUpdateCustomerResponse();

        when(customerService.update(any(CustomerUpdateRequest.class), eq(CUSTOMER_ID))).thenReturn(updatedResponse);

        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(updatedResponse.getUsername()))
                .andExpect(jsonPath("$.email").value(updatedResponse.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(updatedResponse.getPhoneNumber()))
                .andExpect(jsonPath("$.roles[0]").value(CUSTOMER));
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    @DisplayName("Should update customer username  given a valid ID")
    void updateUsername_ShouldReturnUpdatedCustomer_WhenValidRequestAndId() throws Exception {
        String uri = RESOURCE_PATH + "/" + CUSTOMER_ID + "/username";
        CustomerResponse updatedResponse = DummyData.createUpdateCustomerResponse();

        when(customerService.updateUsername(CUSTOMER_ID, "john_doe_updated")).thenReturn(updatedResponse);

        mockMvc.perform(patch(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "john_doe_updated"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(updatedResponse.getUsername()))
                .andExpect(jsonPath("$.email").value(updatedResponse.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(updatedResponse.getPhoneNumber()))
                .andExpect(jsonPath("$.roles[0]").value(CUSTOMER));
    }

    @Test
    @WithMockUser(roles = ADMIN)
    @DisplayName("Should add role to customer given valid username and role")
    void addRole() throws Exception {
        String uri = RESOURCE_PATH + "/" + "add-role";
        Role role = Role.ADMIN;
        customerResponse.getRoles().add(role.name());
        when(this.customerService.addRole(eq("john_doe"), any(Role.class))).thenReturn(customerResponse);

        mockMvc.perform(patch(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "john_doe")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(customerResponse.getUsername()))
                .andExpect(jsonPath("$.email").value(customerResponse.getEmail()))
                .andExpect(jsonPath("$.roles[0]").value(CUSTOMER))
                .andExpect(jsonPath("$.roles[1]").value(ADMIN));
    }
}