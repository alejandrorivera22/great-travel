package com.alex.great_travel.api.models.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReservationRequest {

    @Size(min = 18, max = 20, message = "The size must be between 18 and 20 characters")
    @NotBlank(message = "client id is mandatory")
    private String clientId;

    @Positive
    @NotNull(message = "hotel id is mandatory")
    private Long hotelId;

    @Min(value = 1, message = "Min one day to make reservation")
    @Max(value = 30, message = "Max 30 days to meake reservation")
    @NotNull(message = "total days is mandatory")
    private Integer totalDays;

    @Email(message = "invalid email")
    private String email;

}
