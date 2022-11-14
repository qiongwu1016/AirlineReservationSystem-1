package com.lab2.airlinereservationsystem.controller;

import com.lab2.airlinereservationsystem.common.domain.JsonResponse;
import com.lab2.airlinereservationsystem.common.domain.Response;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Reservation;
import com.lab2.airlinereservationsystem.service.PassengerService;
import com.lab2.airlinereservationsystem.utils.BeanUtil;
import com.lab2.airlinereservationsystem.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Author Yikang Chen, Qiong Wu
 * Map HTTP request for reservation, call corresponding services
 */
@RestController
@RequestMapping("passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    /**
     * @param firstname Passenger's info
     * @param lastname Passenger's info
     * @param birthyear Passenger's info
     * @param gender Passenger's info
     * @param phone Passenger's info
     * @param xml If true return response in xml format, false in json format.
     * Create a new passenger's profile
     */
    @PostMapping
    public ResponseEntity<?> insertPassenger(@RequestParam("firstname") String firstname,
                                             @RequestParam("lastname") String lastname,
                                             @RequestParam("birthyear") int birthyear,
                                             @RequestParam("gender") String gender,
                                             @RequestParam("phone") String phone,
                                             @RequestParam(value = "xml",required = false,defaultValue = "false")boolean xml){
        Passenger passenger = new Passenger(firstname,lastname,birthyear,gender,phone);
        //service
        passengerService.insert(passenger);
        return ResponseUtil.convertResponseEntity(passenger,xml);
    }

    /**
     * Get a passenger by passenger Id
     * @param id
     * @param xml If true return response in xml format, false in json format.
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getPassenger(@PathVariable String id,
                                          @RequestParam(value = "xml",required = false,defaultValue = "false")boolean xml){
        Passenger passenger = passengerService.findOne(id);
        Set<Reservation> reservations = passenger.getReservations();
        if (!CollectionUtils.isEmpty(reservations)){
            for (Reservation reservation : reservations) {
                BeanUtil.convertReservationSimpleForm(reservation);
            }
        } else {
            passenger.setReservations(null);
        }
        return ResponseUtil.convertResponseEntity(passenger,xml);
    }

    /**
     * Update a passenger's profile with all the passenger's fields
     * @param id
     * @param firstname
     * @param lastname
     * @param birthyear
     * @param gender
     * @param phone
     * @param xml If true return response in xml format, false in json format.
     */
    @PutMapping("{id}")
    public ResponseEntity<?>updatePassenger(@PathVariable String id,
                                            @RequestParam("firstname") String firstname,
                                            @RequestParam("lastname") String lastname,
                                            @RequestParam("birthyear") int birthyear,
                                            @RequestParam("gender") String gender,
                                            @RequestParam("phone") String phone,
                                            @RequestParam(value = "xml",required = false,defaultValue = "false")boolean xml){
        Passenger passenger = new Passenger(firstname,lastname,birthyear,gender,phone);
        passenger.setId(id);
        Passenger responsePassenger = passengerService.update(passenger);
        Set<Reservation> reservations = responsePassenger.getReservations();
        if (!CollectionUtils.isEmpty(reservations)) {
            reservations.forEach(reservation -> {
                BeanUtil.convertReservationSimpleForm(reservation);
            });
        }
        return ResponseUtil.convertResponseEntity(responsePassenger,xml);
    }

    /**
     * Delete a passenger by passenger Id
     * @param id
     * @param xml If true return response in xml format, false in json format.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePassenger(@PathVariable("id") String id,
                                             @RequestParam(value = "xml",required = false,defaultValue = "false")boolean xml){
        passengerService.delete(id);
        String returnMsg = String.format("Passenger with id %s is deleted successfully",id);
        return ResponseUtil.convertResponseEntity(xml ? Response.success(returnMsg) : JsonResponse.success(200,returnMsg),xml);
    }

}

