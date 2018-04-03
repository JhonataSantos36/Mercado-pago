package com.mercadopago.lite.model.requests;

import com.google.gson.annotations.SerializedName;

public class SecurityCodeIntent {

    @SerializedName("security_code")
    private String securityCode;

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
}

