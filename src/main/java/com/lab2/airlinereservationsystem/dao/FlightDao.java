package com.lab2.airlinereservationsystem.dao;

import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FlightDao extends JpaRepository<Flight, String> {

    List<Flight> findFlightsByFlightNumberIn(List<String> ids);

    Flight findFlightByFlightNumberAndDepartureDate(String flightNumber, Date departureDate);

    void deleteByFlightNumberAndDepartureDate(String flightNumber, Date departureDate);

    @Query(value = "SELECT * FROM passenger p,passenger_reservations pr,flight_reservation fr WHERE fr.flight_number = :flightNumber AND fr.reservation_number = pr.reservation_number and pr.passenger_id = p.id",
            nativeQuery = true)
    List<Passenger> getPassengerByFlightNumber(@Param("flightNumber")String flightNumber);
}
