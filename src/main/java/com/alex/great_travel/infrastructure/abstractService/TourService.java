package com.alex.great_travel.infrastructure.abstractService;

import com.alex.great_travel.api.models.request.TourRequest;
import com.alex.great_travel.api.models.response.TourResponse;

import java.util.UUID;

public interface TourService extends SimpleCrudService<TourRequest, TourResponse, Long>{
    void removeTicket(Long tourId, UUID ticketId);

    UUID addTicket(Long tourId, Long flyId);

    void removeReservation(Long tourId, UUID reservationId);

    UUID addReservation(Long tourId, Long hotelId,  Integer totalDays);
}
