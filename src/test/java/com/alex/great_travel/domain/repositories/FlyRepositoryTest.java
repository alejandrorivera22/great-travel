package com.alex.great_travel.domain.repositories;

import com.alex.great_travel.domain.entities.FlyEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlyRepositoryTest extends RepositorySpec{

    @Autowired
    FlyRepository flyRepository;

    @Test
    @DisplayName("Should return all flights given less price")
    void selectLessPrice_ShouldReturnSetflights_GivenLessPrice() {
        Set<FlyEntity> flights = this.flyRepository.selectLessPrice(BigDecimal.valueOf(50));
        assertNotNull(flights);
        int expectedElements = 14;
        assertEquals(expectedElements, flights.size());
    }

    @Test
    @DisplayName("Should return all flights given min price and max price")
    void selectBetweenPrice_ShouldReturnSetflights_GivenPrice() {
        Set<FlyEntity> flights = this.flyRepository.selectBetweenPrice(BigDecimal.valueOf(50), BigDecimal.valueOf(100));
        assertNotNull(flights);
        int expectedElements = 1;
        assertEquals(expectedElements, flights.size());
    }

    @Test
    @DisplayName("Should return all flights given origin and destiny")
    void selectOriginDestiny_ShouldReturnSetflights_GivenOriginAndDestiny() {
        String origin = "Mexico";
        String destiny = "Grecia";
        Set<FlyEntity> flights = this.flyRepository.selectOriginDestiny(origin, destiny);
        assertNotNull(flights);
        int expectedElements = 2;
        assertEquals(expectedElements, flights.size());
    }
}