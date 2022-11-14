package com.lab2.airlinereservationsystem.controller;

import com.lab2.airlinereservationsystem.common.domain.JsonResponse;
import com.lab2.airlinereservationsystem.common.domain.Response;
import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.dao.ReservationDao;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Reservation;
import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.service.PassengerService;
import com.lab2.airlinereservationsystem.service.ReservationService;
import com.lab2.airlinereservationsystem.utils.BeanUtil;
import com.lab2.airlinereservationsystem.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationDao reservationDao;

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
//        Passenger passenger = reservation.getPassenger();
//        BeanUtil.convertPassengerSimpleForm(passenger);
//        List<Flight> flights = reservation.getFlights();
//        if (!CollectionUtils.isEmpty(flights)){
//            flights.forEach(flight -> {
//                BeanUtil.convertFlightSimpleForm(flight);
//            });
//        }
        return ResponseUtil.convertResponseEntity(reservation, xml);
    }

    @PostMapping("{number}")
    public ResponseEntity<?> updateReservation(@PathVariable String number,
                                               @RequestParam(value = "flightsAdded",required = false, defaultValue = "") List<String> flightAddList,
                                               @RequestParam(value = "departureDatesAdded", required = false, defaultValue = "") List<String> departureDateAddList,
                                               @RequestParam(value = "departureDatesRemoved", required = false, defaultValue = "") List<String> departureDateRemoveList,
                                               @RequestParam(value = "flightsRemoved",required = false, defaultValue = "") List<String> flightRemoveList,
                                               @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {

        if (flightAddList.size() != departureDateAddList.size()){
            throw new ValidExceptionWrapper("The number of departure dates does not match the number of flights added.");
        }
        if (flightRemoveList.size() != departureDateRemoveList.size()){
            throw new ValidExceptionWrapper("The number of departure dates does not match the number of flights removed.");
        }
       Reservation reservation = reservationService.updateReservation(number, flightAddList, departureDateAddList, flightRemoveList, departureDateRemoveList);
//        if (!CollectionUtils.isEmpty(flightRemoveList)){
//            reservationService.removeFlights(number, flightRemoveList, departureDateRemoveList);
//        }

//        reservationService.updateReservation()

        return ResponseUtil.convertResponseEntity(reservation, xml);
    }

    @DeleteMapping("{number}")
    public ResponseEntity<?> deleteReservation(@PathVariable("number") String number,
                                             @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {
        reservationService.delete(number);
        String returnMsg = String.format("Reservation with number %s is canceled successfully", number);
        return ResponseUtil.convertResponseEntity(xml ? Response.success(returnMsg) : JsonResponse.success(200,returnMsg),xml);
    }

}

