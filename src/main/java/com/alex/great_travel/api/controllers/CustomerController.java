package com.alex.great_travel.api.controllers;

import com.alex.great_travel.api.models.request.CustomerRequest;
import com.alex.great_travel.api.models.request.CustomerUpdateRequest;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.infrastructure.abstractService.CustomerService;
import com.alex.great_travel.util.Role;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Retrieve a customer by DNI")
    @GetMapping("/{dni}")
    public ResponseEntity<CustomerResponse> getByDni(@PathVariable String dni){
        return ResponseEntity.ok(this.customerService.read(dni));
    }

    @Operation(summary = "Update a customer by DNI")
    @PutMapping("/{dni}")
    public ResponseEntity<CustomerResponse> put(@RequestBody @Valid CustomerUpdateRequest customerRequest, @PathVariable String dni){
        return ResponseEntity.ok(this.customerService.update(customerRequest, dni));
    }

    @Operation(summary = "Update a customer username by DNI")
    @PatchMapping("/{dni}/username")
    public ResponseEntity<CustomerResponse> updateUsername(
            @PathVariable String dni,
            @RequestParam String username) {

        return ResponseEntity.ok(customerService.updateUsername(dni, username));
    }

    @Operation(summary = "Update a customer username by DNI")
    @PatchMapping("/{dni}/password")
    public ResponseEntity<CustomerResponse> updatePasword(
            @PathVariable String dni,
            @RequestParam String password) {

        return ResponseEntity.ok(customerService.updatePassword(dni, password));
    }

    @Operation(summary = "Add role to customer by Username")
    @PatchMapping("/add-role")
    public ResponseEntity<CustomerResponse> addRole(
            @RequestParam String username,
            @RequestParam Role role
    ) {
        CustomerResponse response = customerService.addRole(username, role);
        return ResponseEntity.ok(response);
    }

}
