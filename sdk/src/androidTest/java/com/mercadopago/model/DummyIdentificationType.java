package com.mercadopago.model;

/**
 * Created by vaserber on 8/3/16.
 */
public class DummyIdentificationType {

    private String id;
    private String identificationNumber;
    private String getIdentificationNumberWithMask;

    public DummyIdentificationType(String id, String identificationNumber, String getIdentificationNumberWithMask) {
        this.id = id;
        this.identificationNumber = identificationNumber;
        this.getIdentificationNumberWithMask = getIdentificationNumberWithMask;
    }

    public String getId() {
        return id;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getGetIdentificationNumberWithMask() {
        return getIdentificationNumberWithMask;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public void setGetIdentificationNumberWithMask(String getIdentificationNumberWithMask) {
        this.getIdentificationNumberWithMask = getIdentificationNumberWithMask;
    }
}
