package com.lab2.airlinereservationsystem.controller;

import com.lab2.airlinereservationsystem.common.domain.JsonResponse;
import com.lab2.airlinereservationsystem.common.domain.Response;
import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Plane;
import com.lab2.airlinereservationsystem.entity.Reservation;
import com.lab2.airlinereservationsystem.service.FlightService;
import com.lab2.airlinereservationsystem.utils.BeanUtil;
import com.lab2.airlinereservationsystem.utils.DateUtil;
import com.lab2.airlinereservationsystem.utils.ResponseUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @Author Yikang chen, Qiong Wu
 * Services for Flight Controller to call
 */

@RestController
@RequestMapping("flight")
public class FlightController {
    @Autowired
    private FlightService flightService;

    /**
     * Get a flight by flight number and departure date
     * @param flightNumber
     * @param departureDate
     * @param xml If true return response in xml format, false in json format.
     */
    @GetMapping("{flightNumber}/{departureDate}")
    public ResponseEntity<?> getFlightBack(@PathVariable("flightNumber")String flightNumber,
                                           @PathVariable("departureDate")String departureDate,
                                           @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml){
        Flight flight = flightService.findOne(flightNumber,departureDate);
        List<Passenger> passengers = flight.getPassengers();
        if (!CollectionUtils.isEmpty(passengers)) {
            passengers.forEach(passenger -> {
                BeanUtil.convertPassengerSimpleForm(passenger);
            });
        }
        flight.setPassengers(passengers);

        List<Reservation> reservations = flight.getReservations();
        if (!CollectionUtils.isEmpty(reservations)){
            reservations.forEach(reservation -> {
                BeanUtil.convertReservationSimpleForm(reservation);
            });
        }
        return ResponseUtil.convertResponseEntity(flight, xml);
    }

    /**
     * Create or update a Flight.
     * All the fields are passed as query parameters.
     * If flight does not exist then create. Otherwise, update the existing one.
     * @param flightNumber
     * @param departureDate
     * @param price
     * @param departureTime
     * @param origin
     * @param destination
     * @param arrivalTime
     * @param description
     * @param capacity
     * @param model
     * @param manufacturer
     * @param yearOfManufacture
     * @param xml If true return response in xml format, false in json format.
     */
    @PostMapping("{flightNumber}/{departureDate}")
    public ResponseEntity<?> createOrUpdateFlight(@PathVariable("flightNumber") String flightNumber,
                                                  @PathVariable(value = "departureDate") String departureDate,
                                                  @RequestParam(value = "price") int price,
                                                  @RequestParam(value = "departureTime")String departureTime,
                                                  @RequestParam(value = "origin") String origin,
                                                  @RequestParam(value = "destination") String destination,
                                                  @RequestParam(value = "arrivalTime") String arrivalTime,
                                                  @RequestParam(value = "description") String description,
                                                  @RequestParam(value = "capacity") int capacity,
                                                  @RequestParam(value = "model") String model,
                                                  @RequestParam(value = "manufacturer") String manufacturer,
                                                  @RequestParam(value = "yearOfManufacture") int yearOfManufacture,
                                                  @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {
        Flight requestFlight = new Flight();
        requestFlight.setFlightNumber(flightNumber);
        requestFlight.setArrivalTime(DateUtil.getDateHour(arrivalTime));
        requestFlight.setDepartureDate(DateUtil.getDateDay(departureDate));
        requestFlight.setDepartureTime(DateUtil.getDateHour(departureTime));
        requestFlight.setDescription(description);
        requestFlight.setDestination(destination);
        requestFlight.setOrigin(origin);
        requestFlight.setPrice(price);
        requestFlight.setPlane(new Plane(model,capacity,manufacturer,yearOfManufacture));
        requestFlight.setSeatsLeft(capacity);
        flightService.createOrUpdateFlight(requestFlight);
        Flight flight = flightService.findOne(requestFlight.getFlightNumber(), DateUtil.getDateDay(requestFlight.getDepartureDate()));

        List<Passenger> passengers = flight.getPassengers();
        if (!CollectionUtils.isEmpty(passengers)) {
            passengers.forEach(passenger -> {
                BeanUtil.convertPassengerSimpleForm(passenger);
            });
        }
        flight.setPassengers(passengers);

        List<Reservation> reservations = flight.getReservations();
        if (!CollectionUtils.isEmpty(reservations)){
            reservations.forEach(reservation -> {
                BeanUtil.convertReservationSimpleForm(reservation);
            });
        }
        return ResponseUtil.convertResponseEntity(flight, xml);
    }

    /**
     * Delete a flight by flight number and departure date.
     * @param flightNumber
     * @param departureDate
     * @param xml If true return response in xml format, false in json format.
     */
    @DeleteMapping("{flightNumber}/{departureDate}")
    public ResponseEntity<?> deleteFlight(@PathVariable("flightNumber") String flightNumber,
                                             @PathVariable(value = "departureDate") String departureDate,
                                             @RequestParam(value = "xml",required = false,defaultValue = "false")boolean xml){
        flightService.delete(flightNumber,departureDate);
        String returnMsg = String.format("Flight with number %s is deleted successfully",flightNumber);
        return ResponseUtil.convertResponseEntity(xml ? Response.success(returnMsg) : JsonResponse.success(200,returnMsg),xml);
    }
}
