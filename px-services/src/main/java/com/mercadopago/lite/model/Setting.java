package com.mercadopago.lite.model;

import java.util.List;

/**
 * Created by mromar on 10/20/17.
 */

public class Setting {

    private Bin bin;
    private CardNumber cardNumber;
    private SecurityCode securityCode;

    public static Setting getSettingByBin(List<Setting> settings, String bin) {

        Setting selectedSetting = null;

        if (settings != null && settings.size() > 0) {

            for (Setting setting : settings) {

                if (!"".equals(bin) && bin.matches(setting.getBin().getPattern() + ".*") &&
                        (setting.getBin().getExclusionPattern() == null || setting.getBin().getExclusionPattern().isEmpty()
                                || !bin.matches(setting.getBin().getExclusionPattern() + ".*"))) {
                    selectedSetting = setting;
                }
            }
        }

        return selectedSetting;
    }

    public Bin getBin() {
        return bin;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public CardNumber getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(CardNumber cardNumber) {
        this.cardNumber = cardNumber;
    }

    public SecurityCode getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(SecurityCode securityCode) {
        this.securityCode = securityCode;
    }
}
