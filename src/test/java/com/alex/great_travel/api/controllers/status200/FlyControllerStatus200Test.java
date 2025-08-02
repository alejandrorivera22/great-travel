package com.alex.great_travel.api.controllers.status200;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.controllers.FlyController;
import com.alex.great_travel.api.models.response.FlyResponse;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.infrastructure.abstractService.FlyService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.AeroLine;
import com.alex.great_travel.util.SortType;
import com.alex.great_travel.util.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlyController.class)
@Import(SecurityConfig.class)
class FlyControllerStatus200Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    private static final String RESOURCE_PATH = "/fly";

    @MockitoBean
    private FlyService flyService;

    private FlyResponse flyResponse;

    @BeforeEach
    void setUp() {
        flyResponse = DummyData.createFlyResponse(
                99.9999,
                88.8888,
                11.1111,
                22.2222,
                "Mexico",
                "Grecia",
                AeroLine.aero_gold.name(),
                new BigDecimal("45.0"
        ));
    }

    @Test
    void findAll_ShouldReturnFlight() throws Exception {
        List<FlyResponse> flightResponseList = List.of(flyResponse);
        Page<FlyResponse> page = new PageImpl<>(flightResponseList);

        when(flyService.readAll(0, 5, SortType.NONE)).thenReturn(page);

        mockMvc.perform(get(RESOURCE_PATH)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(flightResponseList.get(0).getId()))
                .andExpect(jsonPath("$.content[0].price").value(flightResponseList.get(0).getPrice()))
                .andExpect(jsonPath("$.content[0].destinyName").value(flightResponseList.get(0).getDestinyName()));
    }

    @Test
    @DisplayName("should return a set of flights where the price is lower")
    void getLessPrice() throws Exception {
        String uri = RESOURCE_PATH + "/" + "less_price";
        BigDecimal price = BigDecimal.valueOf(50);
        when(flyService.readLessPrice(price)).thenReturn(Set.of(flyResponse));

        mockMvc.perform(get(uri)
                        .param("price", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(flyResponse.getId()))
                .andExpect(jsonPath("$[0].price").value(flyResponse.getPrice()))
                .andExpect(jsonPath("$[0].destinyName").value(flyResponse.getDestinyName()))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("should return a set of flights where the minimum and maximum prices match")
    void getBetweenPrice() throws Exception {
        String uri = RESOURCE_PATH + "/" + "between_price";
        BigDecimal min = BigDecimal.valueOf(30);
        BigDecimal max = BigDecimal.valueOf(50);
        when(flyService.readBetweenPrices(min, max)).thenReturn(Set.of(flyResponse));

        mockMvc.perform(get(uri)
                        .param("min", "30")
                        .param("max", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(flyResponse.getId()))
                .andExpect(jsonPath("$[0].price").value(flyResponse.getPrice()))
                .andExpect(jsonPath("$[0].destinyName").value(flyResponse.getDestinyName()))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("should return a set of flights where the origin and destination match")
    void getOriginDestiny() throws Exception {
        String uri = RESOURCE_PATH + "/" + "origin_destiny";
        String origin = "Mexico";
        String destiny = "Grecia";
        when(flyService.readByOriginDestiny(origin, destiny)).thenReturn(Set.of(flyResponse));

        mockMvc.perform(get(uri)
                        .param("origin", "Mexico")
                        .param("destiny", "Grecia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(flyResponse.getId()))
                .andExpect(jsonPath("$[0].originName").value(flyResponse.getOriginName()))
                .andExpect(jsonPath("$[0].destinyName").value(flyResponse.getDestinyName()))
                .andExpect(jsonPath("$.length()").value(1));
    }
}