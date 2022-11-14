package com.lab2.airlinereservationsystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Yikang Chen, Qiong Wu
 * The entity class of Flight's Key
 */
@Data
public class FlightKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String flightNumber;

    private Date departureDate;
}
