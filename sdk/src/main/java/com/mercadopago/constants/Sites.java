package com.mercadopago.constants;

import com.mercadopago.model.Site;

/**
 * Created by mromar on 6/3/16.
 */
public class Sites {

    public static final Site ARGENTINA = new Site("MLA", "ARS");
    public static final Site BRASIL = new Site("MLB", "BRL");
    public static final Site CHILE = new Site("MLC", "CLP");
    public static final Site MEXICO = new Site("MLM", "MXN");
    public static final Site COLOMBIA = new Site("MCO", "COP");
    public static final Site VENEZUELA = new Site("MLV", "VEF");
    public static final Site USA = new Site("USA", "USD");

    private Sites(){}
}
