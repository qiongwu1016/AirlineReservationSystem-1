package com.lab2.airlinereservationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
public class Flight {
    // part of the primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_number")
    private String flightNumber;

    /*  Date format: yy-mm-dd, do not include hours, minutes, or seconds.
     ** Example: 2022-03-22
     **The system only needs to support PST. You can ignore other time zones.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date departureDate; //  serve as the primary key together with flightNumber

    /*  Date format: yy-mm-dd-hh, do not include minutes or seconds.
     ** Example: 2017-03-22-19
     */
    // Must be within the same calendar day as departureDate.
    @JsonFormat(pattern = "yyyy-MM-dd-hh")
    private Date departureTime;

    @JsonFormat(pattern = "yyyy-MM-dd-hh")
    private Date arrivalTime;
    // Full form only

    private int price;
    private String origin;
    private String destination;

    private int seatsLeft;
    // Full form only
    private String description;
    @Embedded
    // Embedded,    Full form only
    private Plane plane;
    @Transient
    // Full form only
    private List<Passenger> passengers;
    @ManyToMany(mappedBy = "flights")
    private List<Reservation> reservations;
}
