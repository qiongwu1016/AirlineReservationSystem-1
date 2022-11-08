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
@XmlRootElement(name = "Response")
public class JsonResponse {
    @JsonProperty("Response")
    private Response Response;

    public static JsonResponse success(int code, String msg) {
        Response response = new Response(code, msg);
        return new JsonResponse(response);
    }
}
