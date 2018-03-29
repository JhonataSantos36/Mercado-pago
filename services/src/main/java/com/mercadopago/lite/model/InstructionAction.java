package com.mercadopago.lite.model;

public class InstructionAction {
    private String label;
    private String url;
    private String tag;

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getTag() {
        return tag;
    }

    public static class Tags {
        public static final String LINK = "link";
    }
}
