package com.mercadopago.lite.constants;

import com.mercadopago.model.Site;

public class Sites {

    public static final Site ARGENTINA = new Site("MLA", "ARS");
    public static final Site BRASIL = new Site("MLB", "BRL");
    public static final Site CHILE = new Site("MLC", "CLP");
    public static final Site MEXICO = new Site("MLM", "MXN");
    public static final Site COLOMBIA = new Site("MCO", "COP");
    public static final Site VENEZUELA = new Site("MLV", "VEF");
    public static final Site USA = new Site("USA", "USD");
    public static final Site PERU = new Site("MPE", "PEN");

    private Sites() {
    }

    public static Site getById(String siteId) {
        Site site;
        if (siteId.equals(Sites.ARGENTINA.getId())) {
            site = Sites.ARGENTINA;
        } else if (siteId.equals(Sites.BRASIL.getId())) {
            site = Sites.BRASIL;
        } else if (siteId.equals(Sites.CHILE.getId())) {
            site = Sites.CHILE;
        } else if (siteId.equals(Sites.MEXICO.getId())) {
            site = Sites.MEXICO;
        } else if (siteId.equals(Sites.COLOMBIA.getId())) {
            site = Sites.COLOMBIA;
        } else if (siteId.equals(Sites.VENEZUELA.getId())) {
            site = Sites.VENEZUELA;
        } else if (siteId.equals(Sites.USA.getId())) {
            site = Sites.USA;
        } else if (siteId.equals(Sites.PERU.getId())) {
            site = Sites.PERU;
        } else {
            site = null;
        }
        return site;
    }
}
