package com.lab2.airlinereservationsystem.controller;

import com.lab2.airlinereservationsystem.common.domain.Response;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Reservation;
import com.lab2.airlinereservationsystem.service.PassengerService;
import com.lab2.airlinereservationsystem.service.ReservationService;
import com.lab2.airlinereservationsystem.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestParam(value = "passengerId") String passengerId,
                                               @RequestParam(value = "flightNumbers") List<String> flightNumbers,
                                               @RequestParam(value = "departureDates") List<String> departureDates,
                                               @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {
        if (flightNumbers.size() != departureDates.size()){
            throw new ValidExceptionWrapper("flightNumbers not equal departureDates");
        }
        Reservation reservation = reservationService.createReservation(passengerId,flightNumbers,departureDates);
        return ResponseUtil.convertResponseEntity(reservation, xml);
    }

    @GetMapping("{number}")
    public ResponseEntity<?> getReservation(@PathVariable String number,
                                          @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {
        Reservation reservation = reservationService.findOne(number);
        return ResponseUtil.convertResponseEntity(reservation, xml);
    }

    @PutMapping("{number}")
    public ResponseEntity<?> updateReservation(@PathVariable String number,
                                             @RequestParam(value = "flightsAdded",required = false) List<String> flightAddList,
                                             @RequestParam(value = "flightsRemoved") List<String> flightRemoveList,
                                             @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {
        return ResponseUtil.convertResponseEntity(reservationService.updateReservation(number,flightAddList,flightRemoveList), xml);
    }

    @DeleteMapping("{number}")
    public ResponseEntity<?> deleteReservation(@PathVariable("number") String number,
                                             @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {
        reservationService.delete(number);
        String returnMsg = String.format("Reservation with number %s is canceled successfully", number);
        return ResponseUtil.convertResponseEntity(Response.success(returnMsg), xml);
    }

}

