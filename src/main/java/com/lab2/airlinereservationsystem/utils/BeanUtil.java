package com.lab2.airlinereservationsystem.utils;

import com.lab2.airlinereservationsystem.entity.Passenger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

public class BeanUtil extends BeanUtils {

    public static void copyPropertiesIgnoreNull(Object source,Object target){
        String[] emptyValues = getNullPropertyNames(source);
        copyProperties(source,target,emptyValues);
    }

    public static List<Passenger> simplePassenger(List<Passenger> passengerList){
        if (CollectionUtils.isEmpty(passengerList)){
            return Collections.emptyList();
        }
        List<Passenger> simpleList = new ArrayList<>(passengerList.size());
        passengerList.forEach(passenger -> simpleList.add(simplePassenger(passenger)));
        return simpleList;
    }

    public static Passenger simplePassenger(Passenger passenger){
        Passenger newPassenger = new Passenger();
        copyProperties(passenger,newPassenger);
        newPassenger.setReservations(null);
        newPassenger.setBirthyear(null);
        newPassenger.setPhone(null);
        newPassenger.setGender(null);
        return newPassenger;
    }

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null){
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
