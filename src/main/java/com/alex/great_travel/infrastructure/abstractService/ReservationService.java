package com.alex.great_travel.infrastructure.abstractService;

import com.alex.great_travel.api.models.request.ReservationRequest;
import com.alex.great_travel.api.models.response.ReservationResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface ReservationService extends CrudService<ReservationRequest, ReservationResponse, UUID>{
    BigDecimal findPrice(Long hotelId);

}
