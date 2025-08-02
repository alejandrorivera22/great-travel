package com.alex.great_travel.api.controllers;

import com.alex.great_travel.api.models.request.AuthRequest;
import com.alex.great_travel.api.models.request.CustomerRequest;
import com.alex.great_travel.api.models.response.AuthResponse;
import com.alex.great_travel.infrastructure.abstractService.CustomerService;
import com.alex.great_travel.infrastructure.services.security.UserDetailsServiceImpl;
import com.alex.great_travel.util.jwt.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomerService customerService;
    private final UserDetailsServiceImpl userDetailsService;

    @Operation(summary = "Authenticate customer and return JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtUtils.generateToken((UserDetails) auth.getPrincipal());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @Operation(summary = "Create a new customer")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid CustomerRequest request) {

        customerService.create(request);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtUtils.generateToken((UserDetails) auth.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(jwt));
    }

}
