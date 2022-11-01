package com.lab2.airlinereservationsystem.dao;

import com.lab2.airlinereservationsystem.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PassengerDao extends JpaRepository<Passenger, String> {

    boolean existsByPhone(String phone);

    Passenger findByPhone(String phone);

    @Query(value = "SELECT p.* FROM passenger p,passenger_reservations pr,flight_reservation fr,flight f WHERE f.departure_date = :departureDate and f.flight_number = :flightNumber and f.flight_number = fr.flight_number AND fr.reservation_number = pr.reservation_number and pr.passenger_id = p.id",
            nativeQuery = true)
    List<Passenger> getPassengerByFlightNumberAndDepartureDate(@Param("flightNumber")String flightNumber, @Param("departureDate") Date departureDate);

}
