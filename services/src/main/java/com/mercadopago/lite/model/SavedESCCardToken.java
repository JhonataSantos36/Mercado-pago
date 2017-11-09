package com.mercadopago.lite.model;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mromar on 10/24/17.
 */

public class SavedESCCardToken extends SavedCardToken {

    @SerializedName("require_esc")
    private boolean requireESC;
    private String esc;

    public boolean isRequireESC() {
        return requireESC;
    }

    public void setRequireESC(boolean requireESC) {
        this.requireESC = requireESC;
    }

    public String getEsc() {
        return esc;
    }

    public void setEsc(String esc) {
        this.esc = esc;
    }
}
