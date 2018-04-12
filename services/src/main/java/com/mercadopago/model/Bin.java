package com.mercadopago.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bin implements Parcelable {

    public static final int BIN_LENGTH = 6;

    private String exclusionPattern;
    private String installmentsPattern;
    private String pattern;

    public String getExclusionPattern() {
        return exclusionPattern;
    }

    public void setExclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern;
    }

    public String getInstallmentsPattern() {
        return installmentsPattern;
    }

    public void setInstallmentsPattern(String installmentsPattern) {
        this.installmentsPattern = installmentsPattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    protected Bin(Parcel in) {
        exclusionPattern = in.readString();
        installmentsPattern = in.readString();
        pattern = in.readString();
    }

    public static final Creator<Bin> CREATOR = new Creator<Bin>() {
        @Override
        public Bin createFromParcel(Parcel in) {
            return new Bin(in);
        }

        @Override
        public Bin[] newArray(int size) {
            return new Bin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(exclusionPattern);
        dest.writeString(installmentsPattern);
        dest.writeString(pattern);
    }
}
