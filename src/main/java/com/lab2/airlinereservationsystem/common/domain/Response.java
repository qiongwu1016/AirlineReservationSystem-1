package com.lab2.airlinereservationsystem.common.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "Response")
@NoArgsConstructor
public class Response {
    private int code;
    private String msg;

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static Response success(String msg){
        return new Response(200,msg);
    }
}
