package com.mercadopago.lite.model.requests;

import com.google.gson.annotations.SerializedName;

public class SecurityCodeIntent {

    @SerializedName("security_code")
    private String mSecurityCode;

    public void setSecurityCode(String securityCode){
        this.mSecurityCode = securityCode;
    }
}

