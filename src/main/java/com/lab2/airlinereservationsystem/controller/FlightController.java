package com.lab2.airlinereservationsystem.controller;

import com.lab2.airlinereservationsystem.common.domain.Response;
import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.entity.Plane;
import com.lab2.airlinereservationsystem.entity.Reservation;
import com.lab2.airlinereservationsystem.service.FlightService;
import com.lab2.airlinereservationsystem.utils.DateUtil;
import com.lab2.airlinereservationsystem.utils.ResponseUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("flight")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @GetMapping("{flightNumber}/{departureDate}")
    public ResponseEntity<?> getFlightBack(@PathVariable("flightNumber")String flightNumber,
                                           @PathVariable("departureDate")String departureDate,
                                           @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml){
        Flight flight = flightService.findOne(flightNumber,departureDate);
        return ResponseUtil.convertResponseEntity(flight, xml);
    }

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
        return ResponseUtil.convertResponseEntity(requestFlight, xml);
    }

    @DeleteMapping("{flightNumber}/{departureDate}")
    public ResponseEntity<?> deleteFlight(@PathVariable("flightNumber") String flightNumber,
                                             @PathVariable(value = "departureDate") String departureDate,
                                             @RequestParam(value = "xml",required = false,defaultValue = "false")boolean xml){
        flightService.delete(flightNumber,departureDate);
        String returnMsg = String.format("Flight with number %s is deleted successfully",flightNumber);
        return ResponseUtil.convertResponseEntity(Response.success(returnMsg),xml);
    }
}
