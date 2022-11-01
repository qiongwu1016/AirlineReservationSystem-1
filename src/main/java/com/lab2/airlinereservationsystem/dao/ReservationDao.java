package com.lab2.airlinereservationsystem.dao;

import com.lab2.airlinereservationsystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, String> {

}
