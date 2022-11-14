package com.lab2.airlinereservationsystem.utils;


import org.springframework.http.ResponseEntity;
/**
 * @Arthor Yikang Chen, Qiong Wu
 * Response related Utils function
 */
public class ResponseUtil {
    private ResponseUtil() {
        throw new IllegalStateException("ResponseUtil class");
    }
    private static Object convertResponse(Object result,boolean xml){
        return xml? XmlUtil.convertObject2Xml(result): result;
    }

    public static ResponseEntity<?> convertResponseEntity(Object result,boolean xml){
        return ResponseEntity.ok().body(convertResponse(result,xml));
    }

}
