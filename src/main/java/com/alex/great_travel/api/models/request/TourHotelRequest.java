package com.alex.great_travel.api.models.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourHotelRequest {

    @Positive
    @NotNull(message = "Id hotel is madatory")
    private Long id;

    @Min(value = 1, message = "Min one day to make reservation")
    @Max(value = 30, message = "Max 30 days to meake reservation")
    @NotNull(message = "total days is mandatory")
    private Integer totalDays;
}
