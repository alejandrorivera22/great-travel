package com.alex.great_travel.infrastructure.abstractService;

import com.alex.great_travel.api.models.request.CustomerRequest;
import com.alex.great_travel.api.models.request.CustomerUpdateRequest;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.util.Role;

public interface CustomerService{

    CustomerResponse create(CustomerRequest customerRequest);
    CustomerResponse read(String dni);
    CustomerResponse update(CustomerUpdateRequest customerRequest, String dni);
    CustomerResponse updateUsername(String dni, String username);
    CustomerResponse updatePassword(String dni, String password);
    CustomerResponse addRole(String username, Role role);

}
