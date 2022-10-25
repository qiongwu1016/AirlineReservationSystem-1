package com.lab2.airlinereservationsystem.entity;

import java.util.*;

public class Reservation {
    private String reservationNumber; // primary key
    private Passenger passenger;     // Full form only
    private String origin;
    private String destination;
    private int price; // sum of each flight’s price.   // Full form only
    private List<Flight> flights;    // Full form only, CANNOT be empty, ordered chronologically by departureTime
}
