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
public class CustomerRequest {

    @NotBlank
    private String dni;

    @NotBlank
    @Size(min = 5, max = 30)
    private String username;

    @NotBlank
    @Email(message = "Invalid email")
    private String email;

    @NotBlank
    @Size(min = 5, max = 40)
    private String password;

    @Pattern(
            regexp = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$",
            message = "The credit card must be in the format XXXX-XXXX-XXXX-XXXX"
    )
    private String creditCard;
    @Pattern(
            regexp = "^\\d{2}-\\d{2}-\\d{2}-\\d{2}$",
            message = "The number must be in the format XX-XX-XX-XX"
    )
    private String phoneNumber;
}
