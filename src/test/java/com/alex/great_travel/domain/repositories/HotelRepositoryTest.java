package com.alex.great_travel.domain.repositories;

import com.alex.great_travel.domain.entities.FlyEntity;
import com.alex.great_travel.domain.entities.HotelEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HotelRepositoryTest extends RepositorySpec{

    @Autowired
    HotelRepository hotelRepository;

    @Test
    @DisplayName("Should return all hotels given less price")
    void findByPriceLessThan_ShouldReturnSetHotels_GivenLessPrice() {
        Set<HotelEntity> hotels = this.hotelRepository.findByPriceLessThan(BigDecimal.valueOf(50));
        assertNotNull(hotels);
        int expectedElements = 7;
        assertEquals(expectedElements, hotels.size());
    }

    @Test
    @DisplayName("Should return all hotels given min price and max price")
    void findByPriceBetween_ShouldReturnSetHotels_GivenPrice() {
        Set<HotelEntity> hotels = this.hotelRepository.findByPriceBetween(BigDecimal.valueOf(50), BigDecimal.valueOf(100));
        assertNotNull(hotels);
        int expectedElements = 5;
        assertEquals(expectedElements, hotels.size());
    }

    @Test
    @DisplayName("Should return all hotels given rating")
    void findByRatingGreaterThan_ShouldReturnSetHotels_GivenGreaterRating() {
        Set<HotelEntity> hotels = this.hotelRepository.findByRatingGreaterThan(4);
        assertNotNull(hotels);
        int expectedElements = 8;
        assertEquals(expectedElements, hotels.size());
    }
}