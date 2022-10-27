package com.lab2.airlinereservationsystem.dao;

import com.lab2.airlinereservationsystem.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public interface passengerDao extends JpaRepository<Passenger, String> {

    // get a passenger


}
