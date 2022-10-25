package com.lab2.airlinereservationsystem.entity;

import java.util.*;

public class Flight {
    private String flightNumber; // part of the primary key

    /*  Date format: yy-mm-dd, do not include hours, minutes, or seconds.
     ** Example: 2022-03-22
     **The system only needs to support PST. You can ignore other time zones.
     */
    private Date departureDate; //  serve as the primary key together with flightNumber

    /*  Date format: yy-mm-dd-hh, do not include minutes or seconds.
     ** Example: 2017-03-22-19
     */

    private Date departureTime; // Must be within the same calendar day as departureDate.
    private Date arrivalTime;
    private int price;    // Full form only
    private String origin;
    private String destination;
    private int seatsLeft;
    private String description;   // Full form only
    private Plane plane;  // Embedded,    Full form only
    private List<Passenger> passengers;    // Full form only
}
