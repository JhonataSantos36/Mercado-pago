package com.mercadopago.lite.model;
/**
 * Created by mromar on 10/20/17.
 */

public class Bin {

    private String exclusionPattern;
    private String installmentPattern;
    private String pattern;

    public String getExclusionPattern() {
        return exclusionPattern;
    }

    public void setExclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern;
    }

    public String getInstallmentPattern() {
        return installmentPattern;
    }

    public void setInstallmentPattern(String installmentPattern) {
        this.installmentPattern = installmentPattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
