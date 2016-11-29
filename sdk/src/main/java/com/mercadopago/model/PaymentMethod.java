package com.mercadopago.model;

import java.util.List;

public class PaymentMethod {

    private List<String> additionalInfoNeeded;
    private String id;
    private String name;
    private String paymentTypeId;
    private List<Setting> settings;
    private Integer accreditationTime;

    public List<String> getAdditionalInfoNeeded() {
        return additionalInfoNeeded;
    }

    public void setAdditionalInfoNeeded(List<String> additionalInfoNeeded) {
        this.additionalInfoNeeded = additionalInfoNeeded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isIssuerRequired() {

        return isAdditionalInfoNeeded("issuer_id");
    }

    public boolean isSecurityCodeRequired(String bin) {
        Setting setting = Setting.getSettingByBin(settings, bin);
        if ((setting != null) && (setting.getSecurityCode() != null) &&
                (setting.getSecurityCode().getLength() != 0)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIdentificationTypeRequired() {
        return isAdditionalInfoNeeded("cardholder_identification_type");
    }

    public boolean isIdentificationNumberRequired() {
        return isAdditionalInfoNeeded("cardholder_identification_number");
    }

    private boolean isAdditionalInfoNeeded(String param) {

        if ((additionalInfoNeeded != null) && (additionalInfoNeeded.size() > 0)) {
            for (int i = 0; i < additionalInfoNeeded.size(); i++) {
                if (additionalInfoNeeded.get(i).equals(param)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValidForBin(String bin) {

        return (Setting.getSettingByBin(this.getSettings(), bin) != null);
    }

    public Integer getAccreditationTime() {
        return accreditationTime;
    }
}
