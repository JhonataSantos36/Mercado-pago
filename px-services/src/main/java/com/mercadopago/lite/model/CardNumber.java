package com.mercadopago.lite.model;
/**
 * Created by mromar on 10/20/17.
 */

public class CardNumber {

    private Integer length;
    private String validation;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }
}
