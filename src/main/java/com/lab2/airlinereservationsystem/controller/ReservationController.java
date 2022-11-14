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

/**
 * @Author Yikang chen, Qiong Wu
 * Services for Reservation Controller to call
 */
@RestController
@RequestMapping("reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationDao reservationDao;

    /**
     * @Author Yikang chen, Qiong Wu
     * @param passengerId
     * @param flightNumbers
     * @param departureDates
     * @param xml If true return response in xml format, false in json format.
     * Make a reservation for a passenger on one or more flights.
     */
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

    /**
     * @Author Yikang chen, Qiong Wu
     * @param number Reservation Number
     * @param xml If true return response in xml format, false in json format
     * Get a reservation by reservation number.
     */
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

    /**
     * @Author Yikang chen, Qiong Wu
     * @param number reservation number to specify the reservation to be modified
     * @param flightAddList flight list to be added
     * @param departureDateAddList departure date list of the flights to be added
     * @param departureDateRemoveList departure date list of the flights to be removed
     * @param flightRemoveList flight list to be removed
     * @param xml If true return response in xml format, false in json format.
     * Update a reservation by reservation number.
     */
    @PostMapping("{number}")
    public ResponseEntity<?> updateReservation(@PathVariable String number,
                                               @RequestParam(value = "flightsAdded",required = false) List<String> flightAddList,
                                               @RequestParam(value = "departureDatesAdded", required = false) List<String> departureDateAddList,
                                               @RequestParam(value = "departureDatesRemoved", required = false) List<String> departureDateRemoveList,
                                               @RequestParam(value = "flightsRemoved",required = false) List<String> flightRemoveList,
                                               @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {

        if ((flightAddList != null) && (flightAddList.size() == 0)){
            throw new ErrorExceptionWrapper("If parameter flightAddList exists, then it list of values cannot be empty.");
        }

        if ((flightRemoveList != null) && (flightRemoveList.size() == 0)){
            throw new ErrorExceptionWrapper("If parameter flightRemoveList exists, then it list of values cannot be empty.");
        }

        if ((departureDateAddList != null) && (departureDateAddList.size() == 0)){
            throw new ErrorExceptionWrapper("If parameter departureDateAddList exists, then it list of values cannot be empty.");
        }

        if ((departureDateRemoveList != null) && (departureDateRemoveList.size() == 0)){
            throw new ErrorExceptionWrapper("If parameter departureDateRemoveList exists, then it list of values cannot be empty.");
        }

        if (flightAddList != null){
            if (departureDateAddList == null) throw new ErrorExceptionWrapper("Departure date required to add a flight.");
            if (flightAddList.size() != departureDateAddList.size()){
                throw new ErrorExceptionWrapper("The number of departure dates does not match the number of flights added.");
            }
        }
        if (flightRemoveList != null){
            if (departureDateRemoveList == null) throw new ErrorExceptionWrapper("Departure date required to remove a flight.");
            if (flightRemoveList.size() != departureDateRemoveList.size()){
                throw new ErrorExceptionWrapper("The number of departure dates does not match the number of flights removed.");
            }
        }

       Reservation reservation = reservationService.updateReservation(number, flightAddList, departureDateAddList, flightRemoveList, departureDateRemoveList);
        return ResponseUtil.convertResponseEntity(reservation, xml);
    }

    /**
     * @Author Yikang chen, Qiong Wu
     * @param number reservation number to specify the reservation to be deleted
     * @param xml If true return response in xml format, false in json format.
     * Delete a reservation by reservation nubmer.
     */
    @DeleteMapping("{number}")
    public ResponseEntity<?> deleteReservation(@PathVariable("number") String number,
                                             @RequestParam(value = "xml", required = false, defaultValue = "false") boolean xml) {
        reservationService.delete(number);
        String returnMsg = String.format("Reservation with number %s is canceled successfully", number);
        return ResponseUtil.convertResponseEntity(xml ? Response.success(returnMsg) : JsonResponse.success(200,returnMsg),xml);
    }

}

