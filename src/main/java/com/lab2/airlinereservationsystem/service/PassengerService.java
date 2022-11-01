package com.lab2.airlinereservationsystem.service;

import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.dao.PassengerDao;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PassengerService {

    private static final String QUERY_FORMAT = "Sorry, the requested passenger with ID %s does not exist";

    private static final String DELETE_FORMATTER = "Passenger with id %S does not exist";

    @Autowired
    private PassengerDao passengerDao;

    @Transactional(rollbackFor = Exception.class)
    public void insert(Passenger passenger) {
        //...
        if (passengerDao.existsByPhone(passenger.getPhone())){
            throw new ValidExceptionWrapper("another passenger with the same number already exists.");
        }
        try {

            passengerDao.save(passenger);
        } catch (Exception e) {
            throw new ErrorExceptionWrapper(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        //check exists
        findById(id,DELETE_FORMATTER);
        passengerDao.deleteById(id);
    }

    public Passenger findOne(String id) {
        return findById(id,QUERY_FORMAT);
    }

    private Passenger findById(String id,String formatter) {
        return passengerDao.findById(id)
                .orElseThrow(() -> new ValidExceptionWrapper(String.format(formatter,id)));
    }

    @Transactional(rollbackFor = Exception.class)
    public Passenger update(Passenger passenger) {
        Passenger originalPassenger = findById(passenger.getId(),QUERY_FORMAT);
        Passenger phonePassenger = passengerDao.findByPhone(passenger.getPhone());
        if (!Objects.equals(phonePassenger.getId(), passenger.getId())){
            throw new ErrorExceptionWrapper("another passenger with the same number already exists.");
        }
        BeanUtil.copyPropertiesIgnoreNull(passenger,originalPassenger);
        passengerDao.save(originalPassenger);
        return originalPassenger;
    }

}
