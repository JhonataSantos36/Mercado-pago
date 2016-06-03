package com.mercadopago.model;

import java.io.Serializable;

/**
 * Created by mromar on 6/3/16.
 */
public class Site implements Serializable {

    private String id;
    private String currencyId;

    public Site(String id, String currencyId) {
        this.id = id;
        this.currencyId = currencyId;
    }

    public String getId(){
        return id;
    }

    public String getCurrencyId(){
        return currencyId;
    }
}
