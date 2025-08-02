package com.alex.great_travel.api.controllers;

import com.alex.great_travel.api.models.request.TicketRequest;
import com.alex.great_travel.api.models.response.ErrorResponse;
import com.alex.great_travel.api.models.response.TicketResponse;
import com.alex.great_travel.infrastructure.abstractService.TicketService;
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
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @ApiResponse(
            responseCode = "400",
            description = "When the request have a field invalid we response this",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }
    )
    @Operation(summary = "Save in system un ticket with the fly passed in parameter")
    @PostMapping
    public ResponseEntity<TicketResponse> post(@Valid @RequestBody TicketRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.create(request));
    }

    @Operation(summary = "Return a ticket")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(ticketService.read(id));
    }

    @Operation(summary = "return fly price")
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getFlyPrice(@RequestParam Long flyId){
        return ResponseEntity.ok(Collections.singletonMap("flyprice", this.ticketService.findPrice(flyId)));
    }

    @Operation(summary = "Update ticket")
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> put(@Valid @RequestBody TicketRequest ticketRequest, @PathVariable UUID id){
        return ResponseEntity.ok(ticketService.update(ticketRequest, id));
    }

    @Operation(summary = "Delete a ticket")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
