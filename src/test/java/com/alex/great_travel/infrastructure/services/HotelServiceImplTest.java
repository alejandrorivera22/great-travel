package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.models.response.HotelResponse;
import com.alex.great_travel.domain.entities.HotelEntity;
import com.alex.great_travel.domain.entities.ReservationEntity;
import com.alex.great_travel.domain.repositories.HotelRepository;
import com.alex.great_travel.util.SortType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HotelServiceImplTest extends ServiceSpec{

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    HotelServiceImpl hotelService;


    HotelEntity hotel1;
    HotelEntity hotel2;

    @BeforeEach
    void setUp() {
        hotel1 = DummyData.createHotelEntity();

        hotel2 = HotelEntity.builder()
                .id(2L)
                .price(BigDecimal.valueOf(50))
                .name("Hotel2")
                .rating(4)
                .address("addres hotel 2")
                .reservation(Set.of(new ReservationEntity()))
                .build();
    }

    @Test
    @DisplayName("Should return paged hotels responses")
    void readAll_ShouldReturnPagedHotels() {
        List<HotelEntity> hotelsList = List.of(hotel1);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("price").ascending());
        Page<HotelEntity> page = new PageImpl<>(hotelsList, pageable, hotelsList.size());

        when(hotelRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<HotelResponse> resultPage = hotelService.readAll(0, 5, SortType.LOWER);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
    }

    @Test
    @DisplayName("Should return Set of hotels with less price")
    void readLessPrice_ShouldReturnHotelsWithLessPrice() {
        BigDecimal price = new BigDecimal("110.00");

        when(hotelRepository.findByPriceLessThan(price)).thenReturn(Set.of(hotel1, hotel2));

        Set<HotelResponse> result = hotelService.readLessPrice(price);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(
                result.stream().allMatch(hotel -> hotel.getPrice().compareTo(price) < 0)
        );

        verify(hotelRepository, times(1)).findByPriceLessThan(eq(price));
    }

    @Test
    @DisplayName("Should return hotels matching min and max price")
    void readBetweenPrices_ShouldReturnMatchingHotels() {
        BigDecimal min = BigDecimal.valueOf(50);
        BigDecimal max = BigDecimal.valueOf(100);

        when(hotelRepository.findByPriceBetween(min, max))
                .thenReturn(Set.of(hotel1, hotel2));

        Set<HotelResponse> result = hotelService.readBetweenPrices(min, max);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(
                result.stream().allMatch(hotel -> hotel.getPrice().compareTo(min) >= 0)
        );

        assertTrue(
                result.stream().allMatch(hotel -> hotel.getPrice().compareTo(max) <= 0)

        );

        verify(hotelRepository).findByPriceBetween(min, max);
    }

    @Test
    @DisplayName("Should return Set of hotels with rating Greater Than")
    void readByRatingGreaterThan_ShouldReturnHotelsWithRatingGreaterThan() {
       Integer rating = 4;

        when(hotelRepository.findByRatingGreaterThan(rating)).thenReturn(Set.of(hotel1));

        Set<HotelResponse> result = hotelService.readByRatingGreaterThan(rating);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertTrue(
                result.stream().allMatch(hotel -> hotel.getRating().compareTo(rating) > 0)
        );

        verify(hotelRepository).findByRatingGreaterThan(eq(rating));
    }
}