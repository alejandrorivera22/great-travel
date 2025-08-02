package com.alex.great_travel.infrastructure.abstractService;

import com.alex.great_travel.api.models.request.TicketRequest;
import com.alex.great_travel.api.models.response.TicketResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface TicketService extends CrudService<TicketRequest, TicketResponse, UUID>{

    BigDecimal findPrice(Long flyId);

}
