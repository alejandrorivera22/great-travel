package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.api.models.request.TicketRequest;
import com.alex.great_travel.api.models.response.FlyResponse;
import com.alex.great_travel.api.models.response.TicketResponse;
import com.alex.great_travel.domain.entities.CustomerEntity;
import com.alex.great_travel.domain.entities.FlyEntity;
import com.alex.great_travel.domain.entities.TicketEntity;
import com.alex.great_travel.domain.repositories.CustomerRepository;
import com.alex.great_travel.domain.repositories.FlyRepository;
import com.alex.great_travel.domain.repositories.TicketRepository;
import com.alex.great_travel.infrastructure.abstractService.TicketService;
import com.alex.great_travel.infrastructure.helpers.CustomerHelper;
import com.alex.great_travel.util.Tables;
import com.alex.great_travel.util.TravelUtil;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final FlyRepository flyRepository;
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final CustomerHelper customerHelper;

    public static final BigDecimal CHARGER_PRICE_PERCENTAGE = BigDecimal.valueOf(1.25);

    @Override
    public TicketResponse create(TicketRequest ticketRequest) {
        FlyEntity fly = this.flyRepository.findById(ticketRequest.getFlyId()).orElseThrow();
        CustomerEntity customer = this.customerRepository.findById(ticketRequest.getClientId()).orElseThrow();

        TicketEntity ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().multiply(CHARGER_PRICE_PERCENTAGE))
                .purchaseDate(LocalDate.now())
                .arrivalDate(TravelUtil.getRandomLatter())
                .departureDate(TravelUtil.getRandomSoon())
                .build();

        TicketEntity ticketPersisted = this.ticketRepository.save(ticketToPersist);

        customerHelper.incrase(customer.getDni(), TicketServiceImpl.class);
        log.info("Ticket saved with id:{}", ticketPersisted.getId());

        return entityToResponse(ticketPersisted);
    }

    @Override
    public TicketResponse read(UUID uuid) {
        TicketEntity ticketFromDb = this.ticketRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        return entityToResponse(ticketFromDb);
    }

    @Override
    public BigDecimal findPrice(Long flyId) {
        FlyEntity fly = this.flyRepository.findById(flyId)
                .orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));
        return fly.getPrice().multiply(CHARGER_PRICE_PERCENTAGE);
    }

    @Override
    public TicketResponse update(TicketRequest ticketRequest, UUID uuid) {
        TicketEntity ticketToUpdate = this.ticketRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        FlyEntity fly = this.flyRepository.findById(ticketRequest.getFlyId())
                .orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));

        ticketToUpdate.setFly(fly);
        ticketToUpdate.setPrice(fly.getPrice().multiply(CHARGER_PRICE_PERCENTAGE));
        ticketToUpdate.setArrivalDate(TravelUtil.getRandomLatter());
        ticketToUpdate.setDepartureDate(TravelUtil.getRandomSoon());

        var ticketUpdated = this.ticketRepository.save(ticketToUpdate);

        log.info("Ticket updated with ID {}", ticketUpdated.getId());

        return entityToResponse(ticketUpdated);
    }

    @Override
    public void delete(UUID uuid) {
        TicketEntity ticketToDelete = this.ticketRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        this.ticketRepository.delete(ticketToDelete);
    }

    private TicketResponse entityToResponse(TicketEntity ticketEntity){
        TicketResponse response = new TicketResponse();
        BeanUtils.copyProperties(ticketEntity, response);

        FlyResponse flyResponse = new FlyResponse();
        BeanUtils.copyProperties(ticketEntity.getFly(), flyResponse);

        response.setFly(flyResponse);

        return response;
    }


}
