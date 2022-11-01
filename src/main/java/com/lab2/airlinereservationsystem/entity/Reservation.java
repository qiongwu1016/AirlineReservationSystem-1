package com.lab2.airlinereservationsystem.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@Entity
@Setter
@Getter
@XmlRootElement
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_number")
    private String reservationNumber; // primary key
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "passenger_reservations",
            joinColumns = {@JoinColumn(name = "reservation_number", referencedColumnName = "reservation_number")},
            inverseJoinColumns = {@JoinColumn(name = "passenger_id", referencedColumnName = "id")})
    private Passenger passenger;     // Full form only

    private String origin;
    private String destination;
    private int price; // sum of each flight’s price.   // Full form only
    // Full form only, CANNOT be empty, ordered chronologically by departureTime
    @ManyToMany
    @JoinTable(name = "flight_reservation",
            joinColumns = {@JoinColumn(name = "reservation_number", referencedColumnName = "reservation_number")},
            inverseJoinColumns = {@JoinColumn(name = "flight_number", referencedColumnName = "flight_number")})
    private List<Flight> flights;

    public Reservation(Passenger passenger, List<Flight> flightList) {
        this.passenger = passenger;
        this.flights = flightList;
    }


}
