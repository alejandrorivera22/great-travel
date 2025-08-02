package com.alex.great_travel.api.controllers;

import com.alex.great_travel.api.models.request.ReservationRequest;
import com.alex.great_travel.api.models.response.ErrorResponse;
import com.alex.great_travel.api.models.response.ReservationResponse;
import com.alex.great_travel.infrastructure.abstractService.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @ApiResponse(responseCode = "400", description = "When the request have a field invalid we response this", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    })
    @Operation(summary = "Saves a new reservation in the system")
    @PostMapping
    public ResponseEntity<ReservationResponse> post(@Valid @RequestBody ReservationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request));
    }


    @Operation(summary = "Return a reservation")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(reservationService.read(id));
    }

    @Operation(summary = "Return hotel price")
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getHotelPrice(@RequestParam Long hotelId){
        return ResponseEntity.ok(Collections.singletonMap("hotelprice", this.reservationService.findPrice(hotelId)));
    }

    @Operation(summary = "Update reservation")
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> put(@Valid @RequestBody ReservationRequest reservationRequest, @PathVariable UUID id){
        return ResponseEntity.ok(reservationService.update(reservationRequest, id));
    }

    @Operation(summary = "Delete a reservation")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
