package com.mercadopago.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mromar on 8/31/16.
 */
public class SecurityCodeIntent {

    @SerializedName("security_code")
    private String mSecurityCode;

    public void setSecurityCode(String securityCode){
        this.mSecurityCode = securityCode;
    }
}

