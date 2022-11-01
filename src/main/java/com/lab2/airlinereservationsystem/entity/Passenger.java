package com.lab2.airlinereservationsystem.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Entity
@Getter
@Setter
@XmlRootElement
@NoArgsConstructor
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // primary key
    private String id;

    private String firstname;

    private String lastname;

    private int birthyear;  // Full form only (see definition below)

    private String gender;  // Full form only

    private String phone; // Phone numbers must be unique.   Full form only

    @OneToMany(targetEntity = Reservation.class, cascade = CascadeType.ALL)
    @JoinTable(name = "passenger_reservations",
            joinColumns = {@JoinColumn(name = "passenger_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "reservation_number", referencedColumnName = "reservation_number")})
    private List<Reservation> reservations;   // Full form only

    public Passenger(String firstname, String lastname, int birthyear, String gender, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthyear = birthyear;
        this.gender = gender;
        this.phone = phone;
    }
}
