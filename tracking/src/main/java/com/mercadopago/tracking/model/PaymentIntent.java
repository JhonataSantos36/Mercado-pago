package com.mercadopago.tracking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vaserber on 6/5/17.
 */

public class PaymentIntent implements Serializable {

    @SerializedName("public_key")
    public String mPublicKey;
    @SerializedName("payment_id")
    public String mPaymentId;
    @SerializedName("sdk_flavor")
    public String mFlavor;
    @SerializedName("sdk_platform")
    public String mPlatform;
    @SerializedName("sdk_type")
    public String mType;
    @SerializedName("sdk_version")
    public String mSdkVersion;
    @SerializedName("site_id")
    public String mSite;

    public PaymentIntent(String mPublicKey, String mPaymentId, String mFlavor, String mPlatform, String mType, String mSdkVersion, String mSite) {
        this.mPublicKey = mPublicKey;
        this.mPaymentId = mPaymentId;
        this.mFlavor = mFlavor;
        this.mPlatform = mPlatform;
        this.mType = mType;
        this.mSdkVersion = mSdkVersion;
        this.mSite = mSite;
    }

    public void setPublicKey(String publicKey){
        this.mPublicKey = publicKey;
    }

    public void setPaymentId(String paymentId){
        this.mPaymentId = paymentId;
    }

    public void setFlavor(String flavor){
        this.mFlavor = flavor;
    }

    public void setPlatform(String platform){
        this.mPlatform = platform;
    }

    public void setType(String type){
        this.mType = type;
    }

    public void setSdkVersion(String sdkVersion){
        this.mSdkVersion = sdkVersion;
    }

    public void setSite(String site){
        this.mSite = site;
    }
}
