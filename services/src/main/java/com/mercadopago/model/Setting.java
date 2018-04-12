package com.mercadopago.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Setting implements Parcelable {

    private Bin bin;
    private CardNumber cardNumber;
    private SecurityCode securityCode;

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

    protected Setting(Parcel in) {
        bin = in.readParcelable(Bin.class.getClassLoader());
        cardNumber = in.readParcelable(CardNumber.class.getClassLoader());
        securityCode = in.readParcelable(SecurityCode.class.getClassLoader());
    }

    public static final Creator<Setting> CREATOR = new Creator<Setting>() {
        @Override
        public Setting createFromParcel(Parcel in) {
            return new Setting(in);
        }

        @Override
        public Setting[] newArray(int size) {
            return new Setting[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(bin, flags);
        dest.writeParcelable(cardNumber, flags);
        dest.writeParcelable(securityCode, flags);
    }
}
