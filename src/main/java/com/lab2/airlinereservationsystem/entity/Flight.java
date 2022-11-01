package com.lab2.airlinereservationsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private Integer price;
    private String origin;
    private String destination;

    private int seatsLeft;
    // Full form only
    private String description;
    @Embedded
    // Embedded,    Full form only
    private Plane plane;
    // Full form only
    @Transient
    private List<Passenger> passengers;

    @ManyToMany(targetEntity = Reservation.class, cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinTable(name = "flight_reservation",
            joinColumns = {@JoinColumn(name = "flight_number", referencedColumnName = "flight_number")},
            inverseJoinColumns = {@JoinColumn(name = "reservation_number", referencedColumnName = "reservation_number")}
    )
    private List<Reservation> reservations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(flightNumber, flight.flightNumber) && Objects.equals(departureDate, flight.departureDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNumber, departureDate);
    }
}
