package com.mercadopago.model;

import java.io.Serializable;

public class Cardholder implements Serializable {

    private Identification identification;
    private String name;

    public Identification getIdentification() {
        return identification;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
