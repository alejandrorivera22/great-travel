package com.alex.great_travel.api.controllers;

import com.alex.great_travel.api.models.response.HotelResponse;
import com.alex.great_travel.infrastructure.abstractService.HotelService;
import com.alex.great_travel.util.SortType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/hotel")
@AllArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @Operation(summary = "Return a page with hotels can be sorted or not")
    @GetMapping()
    public ResponseEntity<Page<HotelResponse>> findAll(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestHeader(required = false) SortType sortType
    ) {

        if(Objects.isNull(sortType)) sortType = SortType.NONE;
        Page<HotelResponse> response = hotelService.readAll(page, size, sortType);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);

    }

    @Operation(summary = "Return a list with hotels with price less to price in parameter")
    @GetMapping("/less_price")
    public ResponseEntity<Set<HotelResponse>> getLessPrice(@RequestParam BigDecimal price){
        Set<HotelResponse> response = hotelService.readLessPrice(price);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @Operation(summary = "Return a list with hotels with between prices in parameters")
    @GetMapping("/between_price")
    public ResponseEntity<Set<HotelResponse>> getBetweenPrice(@RequestParam BigDecimal min, @RequestParam BigDecimal max){
        Set<HotelResponse> response = hotelService.readBetweenPrices(min, max);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @Operation(summary = "Return a list with hotels with ratting greater a parameter")
    @GetMapping("/rating")
    public ResponseEntity<Set<HotelResponse>> getByRating(@RequestParam Integer rating){
        if (rating > 4) rating = 4;
        if (rating < 1) rating = 1;
        Set<HotelResponse> response = hotelService.readByRatingGreaterThan(rating);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

}
