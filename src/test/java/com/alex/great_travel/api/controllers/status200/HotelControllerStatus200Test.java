package com.alex.great_travel.api.controllers.status200;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.controllers.HotelController;
import com.alex.great_travel.api.models.response.FlyResponse;
import com.alex.great_travel.api.models.response.HotelResponse;
import com.alex.great_travel.config.security.SecurityConfig;
import com.alex.great_travel.infrastructure.abstractService.FlyService;
import com.alex.great_travel.infrastructure.abstractService.HotelService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
@Import(SecurityConfig.class)
class HotelControllerStatus200Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    private JwtUtils jwtUtils;

    private static final String RESOURCE_PATH = "/hotel";

    @MockitoBean
    private HotelService hotelService;

    private HotelResponse hotelResponse;

    @BeforeEach
    void setUp() {
        hotelResponse = HotelResponse.builder()
                .id(1L)
                .name("Hotel1")
                .address("hotel addres")
                .rating(5)
                .price(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void findAl_ShouldReturnHotelsl() throws Exception {
        List<HotelResponse> hotelsResponseList = List.of(hotelResponse);
        Page<HotelResponse> page = new PageImpl<>(hotelsResponseList);

        when(hotelService.readAll(0, 5, SortType.NONE)).thenReturn(page);

        mockMvc.perform(get(RESOURCE_PATH)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(hotelsResponseList.get(0).getId()))
                .andExpect(jsonPath("$.content[0].name").value(hotelsResponseList.get(0).getName()))
                .andExpect(jsonPath("$.content[0].price").value(hotelsResponseList.get(0).getPrice()));
    }

    @Test
    @DisplayName("should return a hotels set where the price is lower")
    void getLessPrice() throws Exception {
        String uri = RESOURCE_PATH + "/" + "less_price";
        BigDecimal price = BigDecimal.valueOf(120);
        when(hotelService.readLessPrice(price)).thenReturn(Set.of(hotelResponse));

        mockMvc.perform(get(uri)
                        .param("price", "120"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(hotelResponse.getId()))
                .andExpect(jsonPath("$[0].price").value(hotelResponse.getPrice()))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("should return a hotels set where the minimum and maximum prices match")
    void getBetweenPrice() throws Exception {
        String uri = RESOURCE_PATH + "/" + "between_price";
        BigDecimal min = BigDecimal.valueOf(90);
        BigDecimal max = BigDecimal.valueOf(110);
        when(hotelService.readBetweenPrices(min, max)).thenReturn(Set.of(hotelResponse));

        mockMvc.perform(get(uri)
                        .param("min", "90")
                        .param("max", "110"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(hotelResponse.getId()))
                .andExpect(jsonPath("$[0].price").value(hotelResponse.getPrice()))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("should return a hotels set where the rating is greater")
    void getByRating() throws Exception {
        String uri = RESOURCE_PATH + "/" + "rating";
        Integer rating = 4;
        when(hotelService.readByRatingGreaterThan(rating)).thenReturn(Set.of(hotelResponse));

        mockMvc.perform(get(uri)
                        .param("rating", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(hotelResponse.getId()))
                .andExpect(jsonPath("$[0].rating").value(hotelResponse.getRating()))
                .andExpect(jsonPath("$.length()").value(1));
    }
}