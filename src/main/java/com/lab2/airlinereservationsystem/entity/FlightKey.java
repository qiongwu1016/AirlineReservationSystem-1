package com.lab2.airlinereservationsystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FlightKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String flightNumber;

    private Date departureDate;
}
