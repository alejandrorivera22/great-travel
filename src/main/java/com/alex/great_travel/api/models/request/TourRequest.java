package com.alex.great_travel.api.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourRequest {

    @Size(min = 18, max = 20, message = "The size must be between 18 and 20 characters")
    @NotBlank(message = "client id is mandatory")
    private String customerId;

    @Size(min = 1, message = "Min flight tour per tour")
    private Set<TourFlyRequest> flights;

    @Size(min = 1, message = "Min hotel tour per tour")
    private Set<TourHotelRequest> hotels;

    @Email(message = "Invalid email")
    private String email;
}
