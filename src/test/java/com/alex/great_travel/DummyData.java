package com.alex.great_travel;

import com.alex.great_travel.api.models.request.*;
import com.alex.great_travel.api.models.response.CustomerResponse;
import com.alex.great_travel.api.models.response.FlyResponse;
import com.alex.great_travel.api.models.response.TicketResponse;
import com.alex.great_travel.api.models.response.TourResponse;
import com.alex.great_travel.domain.entities.*;
import com.alex.great_travel.util.AeroLine;
import com.alex.great_travel.util.Role;
import com.alex.great_travel.util.TravelUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DummyData {

    // Roles
    public static RoleEntity createRoleEntityAdmin() {
        return RoleEntity.builder()
                .id((short) 1)
                .name(Role.ADMIN)
                .build();
    }

    public static RoleEntity createRoleEntityCustomer() {
        return RoleEntity.builder()
                .id((short) 2L)
                .name(Role.CUSTOMER)
                .build();
    }

    // Customer Requests
    public static CustomerRequest createCustomerRequest() {
        return CustomerRequest.builder()
                .dni("VIKI771012HMCRG093")
                .username("john_doe")
                .email("john@example.com")
                .password("password123")
                .creditCard("6473-9486-9372-0921")
                .phoneNumber("33-74-58-43")
                .build();
    }

    public static CustomerRequest createCustomerRequestUpdate() {
        return CustomerRequest.builder()
                .dni("VIKI771012HMCRG093") // mismo DNI para update
                .username("john_doe_updated")
                .email("john_updated@example.com")
                .password("newPassword456")
                .creditCard("1111-2222-3333-4444")
                .phoneNumber("33-99-88-77")
                .build();
    }

    // Customer Entities
    public static CustomerEntity createCustomerEntity(RoleEntity role) {
        return CustomerEntity.builder()
                .dni("VIKI771012HMCRG093")
                .username("dummy_user")
                .email("john@example.com")
                // contraseña ya hasheada (dummy)
                .password("$2a$10$OJ/Sf.WtAJqGyBgpn3kix.bRT7OXmEFF4LBSMjb.KSHne.1aFVq4W")
                .creditCard("6473-9486-9372-0921")
                .totalFlights(0)
                .totalLodgings(0)
                .totalTours(0)
                .phoneNumber("33-74-58-43")
                .enabled(true)
                .build();
    }

    public static CustomerEntity createCustomerEntityDisabled(RoleEntity role) {
        return CustomerEntity.builder()
                .dni("DISA000000000000")
                .username("disabled_user")
                .email("disabled@example.com")
                .password("$2a$10$dummyencodedpassword")
                .creditCard("0000-0000-0000-0000")
                .totalFlights(0)
                .totalLodgings(0)
                .totalTours(0)
                .phoneNumber("00-00-00-00")
                .enabled(false)
                .build();
    }

    // Customer Responses
    public static CustomerResponse createCustomerResponse() {
        return CustomerResponse.builder()
                .dni("VIKI771012HMCRG093")
                .username("john_doe")
                .email("john@example.com")
                .totalFlights(0)
                .totalLodgings(0)
                .totalTours(0)
                .phoneNumber("33-74-58-43")
                .roles(new ArrayList<>())
                .build();
    }

    public static CustomerUpdateRequest createCustomerUpdateRequest() {
        return CustomerUpdateRequest.builder()
                .phoneNumber("33-74-58-43")
                .creditCard("1111-2222-3333-4444")
                .build();
    }

    public static CustomerResponse createUpdateCustomerResponse() {
        return CustomerResponse.builder()
                .dni("VIKI771012HMCRG093")
                .username("john_doe_updated")
                .email("john_updated@example.com")
                .totalFlights(2)
                .totalLodgings(1)
                .totalTours(3)
                .phoneNumber("33-99-88-77")
                .roles(List.of("CUSTOMER"))
                .build();
    }

    public static List<CustomerResponse> createCustomerResponseList() {
        CustomerResponse first = createCustomerResponse();
        CustomerResponse second = CustomerResponse.builder()
                .dni("BBMB771012HMCRR022")
                .username("jane_smith")
                .email("jane@example.com")
                .totalFlights(0)
                .totalLodgings(0)
                .totalTours(0)
                .phoneNumber("55-83-32-22")
                .roles(List.of("CUSTOMER"))
                .build();
        return List.of(first, second);
    }

    // Listas auxiliares
    public static List<CustomerEntity> createDisabledCustomerList() {
        RoleEntity role = createRoleEntityCustomer();
        return List.of(createCustomerEntityDisabled(role));
    }

    public static List<CustomerEntity> createCustomerEntityList() {
        RoleEntity customerRole = createRoleEntityCustomer();
        RoleEntity adminRole = createRoleEntityAdmin();

        CustomerEntity john = createCustomerEntity(customerRole);
        CustomerEntity jane = CustomerEntity.builder()
                .dni("BBMB771012HMCRR022")
                .username("jane_smith")
                .email("jane@example.com")
                .password("$2a$10$K/geCZgSsPGpXlTLg/N5AOgj3YIVSDkk5nX9aJCCla.bdciJCHf4u")
                .creditCard("4463-3326-9980-5454")
                .totalFlights(0)
                .totalLodgings(0)
                .totalTours(0)
                .phoneNumber("55-83-32-22")
                .enabled(true)
                .roles(new HashSet<>(Set.of(customerRole)))
                .build();

        CustomerEntity admin = CustomerEntity.builder()
                .dni("WALA771012HCRGR054")
                .username("admin")
                .email("admin@example.com")
                .password("$2a$10$tWDdJRPcuJ1lAzcC5TKlH.PCAMkoqv4tc1QmUnY//ganc7AfBckHG")
                .creditCard("6677-5244-94572-0165")
                .totalFlights(0)
                .totalLodgings(0)
                .totalTours(0)
                .phoneNumber("33-24-41-54")
                .enabled(true)
                .roles(new HashSet<>(Set.of(adminRole, customerRole)))
                .build();

        return List.of(john, jane, admin);
    }

    //Fly

    public static FlyEntity createFlyEntity(
            double originLat,
            double originLng,
            double destinyLat,
            double destinyLng,
            String originName,
            String destinyName,
            String aeroLine,
            BigDecimal price
    ) {
        return FlyEntity.builder()
                .originLat(originLat)
                .originLng(originLng)
                .destinyLat(destinyLat)
                .destinyLng(destinyLng)
                .originName(originName)
                .destinyName(destinyName)
                .aeroLine(AeroLine.aero_gold)
                .price(price)
                .build();
    }

    public static FlyResponse createFlyResponse(
            double originLat,
            double originLng,
            double destinyLat,
            double destinyLng,
            String originName,
            String destinyName,
            String aeroLine,
            BigDecimal price
    ) {
        return FlyResponse.builder()
                .originLat(originLat)
                .originLng(originLng)
                .destinyLat(destinyLat)
                .destinyLng(destinyLng)
                .originName(originName)
                .destinyName(destinyName)
                .aeroLine(AeroLine.aero_gold)
                .price(price)
                .build();
    }

    public static Set<FlyResponse> createFlightResponseSet() {
        return Set.of(
                createFlyResponse(99.9999, 88.8888, 11.1111, 22.2222, "Mexico", "Grecia", "aero_gold", new BigDecimal("43.00")),
                createFlyResponse(11.1111, 22.2222, 99.9999, 88.8888, "Grecia", "Mexico", "aero_gold", new BigDecimal("33.33")),
                createFlyResponse(99.9999, 88.8888, 88.8888, 77.7777, "Mexico", "Iceland", "aero_gold", new BigDecimal("48.70")),
                createFlyResponse(99.9999, 88.8888, 88.8888, 77.7777, "Iceland", "Mexico", "aero_gold", new BigDecimal("12.99")),
                createFlyResponse(88.8888, 77.7777, 11.1111, 22.2222, "Iceland", "Gracia", "aero_gold", new BigDecimal("85.98")),
                createFlyResponse(11.1111, 22.2222, 88.8888, 77.7777, "Gracia", "Iceland", "aero_gold", new BigDecimal("29.99")),
                createFlyResponse(99.9999, 88.8888, 11.1111, 22.2222, "Mexico", "Grecia", "blue_sky", new BigDecimal("25.65")),
                createFlyResponse(11.1111, 22.2222, 99.9999, 88.8888, "Grecia", "Mexico", "blue_sky", new BigDecimal("12.99")),
                createFlyResponse(44.4444, 55.555, 11.1111, 22.2222, "Canada", "Mexico", "aero_gold", new BigDecimal("19.99")),
                createFlyResponse(11.1111, 22.2222, 44.4444, 55.5555, "Mexico", "Canada", "aero_gold", new BigDecimal("15.65")),
                createFlyResponse(99.9999, 88.8888, 88.8888, 77.7777, "Mexico", "Iceland", "blue_sky", new BigDecimal("42.99")),
                createFlyResponse(99.9999, 88.8888, 88.8888, 77.7777, "Iceland", "Mexico", "blue_sky", new BigDecimal("21.54")),
                createFlyResponse(88.8888, 77.7777, 11.1111, 22.2222, "Iceland", "Gracia", "blue_sky", new BigDecimal("12.00")),
                createFlyResponse(44.4444, 55.555, 11.1111, 22.2222, "Canada", "Mexico", "blue_sky", new BigDecimal("16.99"))
        );
    }

    //Hotes
    public static HotelEntity createHotelEntity() {
        return HotelEntity.builder()
                .id(1L)
                .price(BigDecimal.valueOf(100))
                .name("Hotel1")
                .rating(5)
                .address("addres hotel 1")
                .reservation(Set.of(new ReservationEntity()))
                .build();
    }

    //Reservation

    public static ReservationEntity createReservationEntity(
            CustomerEntity customer,
            HotelEntity hotel,
            Integer totalDays,
            BigDecimal charger){
       return ReservationEntity.builder()
                .id(UUID.randomUUID())
                .customer(customer)
                .hotel(hotel)
                .dateTimeReservation(LocalDateTime.now())
                .totalDays(totalDays)
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(totalDays))
                .price(hotel.getPrice().multiply(charger))
                .build();
    }

    public static ReservationRequest createReservationRequest(String clientId, Long hotelId, Integer totalDays, String email){
        return ReservationRequest.builder()
                .clientId(clientId)
                .hotelId(hotelId)
                .totalDays(totalDays)
                .email(email)
                .build();
    }

    //Ticket

    public static TicketEntity createTicketEntity(FlyEntity fly, TourEntity tour, BigDecimal charger, CustomerEntity customer){
        return TicketEntity.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .fly(fly)
                .tour(tour)
                .purchaseDate(LocalDate.now())
                .arrivalDate(TravelUtil.getRandomLatter())
                .departureDate(TravelUtil.getRandomSoon())
                .price(fly.getPrice().multiply(charger))
                .customer(customer)
                .build();
    }

    public static TicketRequest createTicketRequest(String clientId, Long flyId, String email){
        return TicketRequest.builder()
                .clientId(clientId)
                .flyId(flyId)
                .email(email)
                .build();
    }

    //Tour

    private static final BigDecimal CHARGER_PRICE_PERCENTAGE = BigDecimal.valueOf(1.25);

    public static TourFlyRequest createTourFlyRequest(Long flyId) {
        return TourFlyRequest.builder()
                .id(flyId)
                .build();
    }

    public static TourHotelRequest createTourHotelRequest(Long hotelId, Integer totalDays) {
        return TourHotelRequest.builder()
                .id(hotelId)
                .totalDays(totalDays)
                .build();
    }

    public static TourRequest createTourRequest(String customerId, Set<TourFlyRequest> flights, Set<TourHotelRequest> hotels, String email) {
        return TourRequest.builder()
                .customerId(customerId)
                .flights(flights)
                .hotels(hotels)
                .email(email)
                .build();
    }

    public static TourResponse createTourResponse(Long id, Set<UUID> ticketIds, Set<UUID> reservationIds) {
        return TourResponse.builder()
                .id(id)
                .ticketsIds(ticketIds)
                .reservationIds(reservationIds)
                .build();
    }


    public static TourEntity createTourEntity(Long tourId,
                                              CustomerEntity customer,
                                              Set<FlyEntity> flights,
                                              Set<HotelEntity> hotelsWithDays) {

        if (customer == null) {
            customer = createCustomerEntity(createRoleEntityCustomer());
        }

        Set<TicketEntity> tickets = new HashSet<>();
        if (flights != null) {
            for (FlyEntity fly : flights) {

                TicketEntity ticket = createTicketEntity(fly, null, CHARGER_PRICE_PERCENTAGE, customer);
                tickets.add(ticket);
            }
        }

        Set<ReservationEntity> reservations = new HashSet<>();
        if (hotelsWithDays != null) {
            for (HotelEntity hotel : hotelsWithDays) {
                ReservationEntity reservation = createReservationEntity(customer, hotel, 1, CHARGER_PRICE_PERCENTAGE);
                reservations.add(reservation);
            }
        }

        TourEntity tour = TourEntity.builder()
                .id(tourId)
                .customer(customer)
                .tickets(tickets)
                .reservations(reservations)
                .build();

        tickets.forEach(t -> t.setTour(tour));
        reservations.forEach(r -> r.setTour(tour));

        return tour;
    }

    public static TourEntity createTourEntityDefault() {
        FlyEntity fly = createFlyEntity(99.99, 88.88, 11.11, 22.22, "Origen", "Destino", "aero_gold", new BigDecimal("50.00"));
        HotelEntity hotel = createHotelEntity();

        CustomerEntity customer = createCustomerEntity(createRoleEntityCustomer());

        Set<FlyEntity> flights = Set.of(fly);
        Set<HotelEntity> hotels = Set.of(hotel);

        return createTourEntity(1L, customer, flights, hotels);
    }

    public static TourResponse createTourResponseDefault(TourEntity tour) {
        return createTourResponse(
                tour.getId(),
                tour.getTickets().stream().map(TicketEntity::getId).collect(Collectors.toSet()),
                tour.getReservations().stream().map(ReservationEntity::getId).collect(Collectors.toSet())
        );
    }
}
