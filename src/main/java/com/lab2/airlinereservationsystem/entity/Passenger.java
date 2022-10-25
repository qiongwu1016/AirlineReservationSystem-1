package com.lab2.airlinereservationsystem.entity;

import java.util.*;

public class Passenger {
    private String id;   // primary key
    private String firstname;
    private String lastname;
    private int birthyear;  // Full form only (see definition below)
    private String gender;  // Full form only
    private String phone; // Phone numbers must be unique.   Full form only
    private List<Reservation> reservations;   // Full form only
}
