package com.mercadopago.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vaserber on 7/20/17.
 */

public class SavedESCCardToken extends SavedCardToken {

    @SerializedName("require_esc")
    private final boolean requireESC;
    private String esc;

    public SavedESCCardToken(String cardId, String securityCode, boolean requireESC) {
        super(cardId, securityCode);
        this.requireESC = requireESC;
    }

    public SavedESCCardToken(String cardId, String securityCode, boolean requireESC, String esc) {
        super(cardId, securityCode);
        this.requireESC = requireESC;
        this.esc = esc;
    }
}
