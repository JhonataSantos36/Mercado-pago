package com.mercadopago.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CardNumber implements Parcelable {

    private Integer length;
    private String validation;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }


    protected CardNumber(Parcel in) {
        if (in.readByte() == 0) {
            length = null;
        } else {
            length = in.readInt();
        }
        validation = in.readString();
    }

    public static final Creator<CardNumber> CREATOR = new Creator<CardNumber>() {
        @Override
        public CardNumber createFromParcel(Parcel in) {
            return new CardNumber(in);
        }

        @Override
        public CardNumber[] newArray(int size) {
            return new CardNumber[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        if (length == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(length);
        }
        dest.writeString(validation);
    }
}
