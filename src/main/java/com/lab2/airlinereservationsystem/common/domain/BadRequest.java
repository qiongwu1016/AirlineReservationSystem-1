package com.lab2.airlinereservationsystem.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "BadRequest")
public class BadRequest {

    private int code;
    private String msg;

    public static BadRequest fail(int code, String msg) {
        return new BadRequest(code, msg != null && !msg.isEmpty() ? msg : "error");
    }
}
