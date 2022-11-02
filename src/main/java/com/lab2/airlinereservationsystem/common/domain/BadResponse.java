package com.lab2.airlinereservationsystem.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class BadResponse {
    @JsonProperty("BadRequest")
    private Response BadRequest;

    public static BadResponse fail(int code, String msg) {
        Response response =new Response(code, msg != null && !msg.isEmpty() ? msg : "error");
        return new BadResponse(response);
    }
}
