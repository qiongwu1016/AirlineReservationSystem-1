package com.lab2.airlinereservationsystem.dao;

import com.lab2.airlinereservationsystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, String> {
    @Query(value = "SELECT fr.flight_number as flightNumber,fr.departure_date as departureDate  FROM flight_reservation fr WHERE fr.reservation_number = :reservationNumber",
            nativeQuery = true)
    List<Map<String,Object>>findFlightNoAndDate(@Param("reservationNumber")String reservationNumber);
}
