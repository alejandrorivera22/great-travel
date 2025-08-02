package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.DummyData;
import com.alex.great_travel.api.models.response.FlyResponse;
import com.alex.great_travel.domain.entities.FlyEntity;
import com.alex.great_travel.domain.repositories.FlyRepository;
import com.alex.great_travel.util.AeroLine;
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

class FlyServiceImplTest extends ServiceSpec{

    @Mock
    private FlyRepository flyRepository;

    @InjectMocks
    private FlyServiceImpl flyService;


    private FlyEntity flyEntity1;
    private FlyEntity flyEntity2;

    @BeforeEach
    void setUp() {

        flyEntity1 = DummyData.createFlyEntity(
                99.9999,
                88.8888,
                11.1111,
                22.2222,
                "Mexico",
                "Grecia",
                AeroLine.aero_gold.name(),
                new BigDecimal("43.00"
                ));

        flyEntity2 = DummyData.createFlyEntity(
                99.9999,
                88.8888,
                11.1111,
                22.2222,
                "Mexico",
                "Grecia",
                AeroLine.aero_gold.name(),
                new BigDecimal("45.00"
                ));
    }

    @Test
    @DisplayName("Should return paged flights responses")
    void readAll_ShouldReturnPagedFlights() {
        List<FlyEntity> flyList = List.of(flyEntity1);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("price").ascending());
        Page<FlyEntity> page = new PageImpl<>(flyList, pageable, flyList.size());

        when(flyRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<FlyResponse> resultPage = flyService.readAll(0, 5, SortType.LOWER);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
    }

    @Test
    @DisplayName("Should return Set of flights with less price")
    void readLessPrice_ShouldReturnFlightsWithLessPrice() {
        BigDecimal price = new BigDecimal("50.00");

        when(flyRepository.selectLessPrice(price)).thenReturn(Set.of(flyEntity1, flyEntity2));

        Set<FlyResponse> result = flyService.readLessPrice(price);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(
                result.stream().allMatch(fly -> fly.getPrice().compareTo(price) < 0)
        );

        verify(flyRepository).selectLessPrice(eq(price));
    }

    @Test
    @DisplayName("Should return flights matching min and max price")
    void readBetweenPrices_ShouldReturnMatchingFlights() {
        BigDecimal min = BigDecimal.valueOf(40);
        BigDecimal max = BigDecimal.valueOf(50);

        when(flyRepository.selectBetweenPrice(min, max))
                .thenReturn(Set.of(flyEntity1, flyEntity2));

        Set<FlyResponse> result = flyService.readBetweenPrices(min, max);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(
                result.stream().allMatch(fly -> fly.getPrice().compareTo(min) >= 0)
        );

        assertTrue(
                result.stream().allMatch(fly -> fly.getPrice().compareTo(max) <= 0)

        );

        verify(flyRepository).selectBetweenPrice(min, max);
    }

    @Test
    @DisplayName("Should return flights matching origin and destiny")
    void readByOriginDestiny_ShouldReturnMatchingFlights() {
        String origin = "Mexico";
        String destiny = "Grecia";

        when(flyRepository.selectOriginDestiny(origin, destiny))
                .thenReturn(Set.of(flyEntity1, flyEntity2));

        Set<FlyResponse> result = flyService.readByOriginDestiny(origin, destiny);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(
                result.stream().allMatch(fly -> fly.getOriginName().equals(origin)),
                "origin should be Mexico"
        );

        assertTrue(
                result.stream().allMatch(fly -> fly.getDestinyName().equals(destiny)),
                "destiny should be Grecia"
        );

        verify(flyRepository).selectOriginDestiny(origin, destiny);
    }
}