package com.lab2.airlinereservationsystem.dao;

import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightDao extends JpaRepository<Flight, String> {

    List<Flight> findFlightsByFlightNumberIn(List<String> ids);

    Flight findFlightByFlightNumberAndDepartureDate(String flightNumber,String departureDate);

    void deleteByFlightNumberAndDepartureDate(String flightNumber,String departureDate);
}
