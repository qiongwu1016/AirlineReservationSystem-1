package com.lab2.airlinereservationsystem.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Author Yikang Chen, Qiong Wu
 * The entity class of Plane
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plane {
    private String model;
    private int capacity;
    private String manufacturer;
    private int yearOfManufacture;
}
