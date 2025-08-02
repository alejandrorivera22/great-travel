package com.alex.great_travel.infrastructure.services;

import com.alex.great_travel.api.models.request.ReservationRequest;
import com.alex.great_travel.api.models.response.HotelResponse;
import com.alex.great_travel.api.models.response.ReservationResponse;
import com.alex.great_travel.config.RedisConfig;
import com.alex.great_travel.domain.entities.CustomerEntity;
import com.alex.great_travel.domain.entities.HotelEntity;
import com.alex.great_travel.domain.entities.ReservationEntity;
import com.alex.great_travel.domain.repositories.CustomerRepository;
import com.alex.great_travel.domain.repositories.HotelRepository;
import com.alex.great_travel.domain.repositories.ReservationRepository;
import com.alex.great_travel.infrastructure.abstractService.ReservationService;
import com.alex.great_travel.infrastructure.helpers.CustomerHelper;
import com.alex.great_travel.util.Tables;
import com.alex.great_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class ReservationServiceImp implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final HotelRepository hotelRepository;
    private final CustomerHelper customerHelper;

    public static final BigDecimal CHARGER_PRICE_PERCENTAGE = BigDecimal.valueOf(1.25);

    @Override
    public ReservationResponse create(ReservationRequest reservationRequest) {
        HotelEntity hotel = this.hotelRepository.findById(reservationRequest.getHotelId())
                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        CustomerEntity customer = this.customerRepository.findById(reservationRequest.getClientId())
                .orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        ReservationEntity reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .customer(customer)
                .hotel(hotel)
                .dateTimeReservation(LocalDateTime.now())
                .totalDays(reservationRequest.getTotalDays())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(reservationRequest.getTotalDays()))
                .price(hotel.getPrice().multiply(CHARGER_PRICE_PERCENTAGE))
                .build();

        ReservationEntity reservationPersisted = this.reservationRepository.save(reservationToPersist);
        this.customerHelper.incrase(customer.getDni(), ReservationServiceImp.class);

        return this.entityToResponse(reservationPersisted);
    }

    @Override
    public ReservationResponse read(UUID uuid) {
        ReservationEntity reservationFromDb = this.reservationRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));
        return entityToResponse(reservationFromDb);
    }

    @Override
    public BigDecimal findPrice(Long hotelId) {
        HotelEntity hotel = this.hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        return hotel.getPrice().multiply(CHARGER_PRICE_PERCENTAGE);
    }

    @Override
    public ReservationResponse update(ReservationRequest reservationRequest, UUID uuid) {
        HotelEntity hotel = this.hotelRepository.findById(reservationRequest.getHotelId())
                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        ReservationEntity reservationToUpdate = this.reservationRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));

        reservationToUpdate.setHotel(hotel);
        reservationToUpdate.setDateTimeReservation(LocalDateTime.now());
        reservationToUpdate.setDateStart(LocalDate.now());
        reservationToUpdate.setTotalDays(reservationRequest.getTotalDays());
        reservationToUpdate.setDateEnd(LocalDate.now().plusDays(reservationRequest.getTotalDays()));
        reservationToUpdate.setPrice(hotel.getPrice().multiply(CHARGER_PRICE_PERCENTAGE));

        ReservationEntity reservationUpdated = this.reservationRepository.save(reservationToUpdate);

        log.info("reservation upated with id: {}", reservationUpdated.getId());

        return this.entityToResponse(reservationUpdated);
    }

    @Override
    public void delete(UUID uuid) {
        ReservationEntity reservationToDelete = this.reservationRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));
        this.reservationRepository.delete(reservationToDelete);
    }

    private ReservationResponse entityToResponse(ReservationEntity reservationEntity) {
        ReservationResponse response = new ReservationResponse();
        BeanUtils.copyProperties(reservationEntity, response);

        HotelResponse hotelResponse = new HotelResponse();
        BeanUtils.copyProperties(reservationEntity.getHotel(), hotelResponse);

        response.setHotel(hotelResponse);

        return response;

    }


}
