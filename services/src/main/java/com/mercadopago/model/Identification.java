package com.mercadopago.model;

import java.io.Serializable;

public class Identification implements Serializable {

    private String number;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
