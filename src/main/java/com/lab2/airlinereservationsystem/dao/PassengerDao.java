package com.lab2.airlinereservationsystem.dao;

import com.lab2.airlinereservationsystem.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerDao extends JpaRepository<Passenger, String> {

    boolean existsByPhone(String phone);

    Passenger findByPhone(String phone);
}
