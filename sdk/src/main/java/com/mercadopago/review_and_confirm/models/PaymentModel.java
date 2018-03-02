package com.mercadopago.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by lbais on 1/3/18.
 */

public class PaymentModel implements Parcelable {

    public final String paymentMethodId;
    public final String lastFourDigits;
    public final Integer accreditationTime;
    public final String issuerName;
    public final boolean moreThanOnePaymentMethod;
    private final String paymentMethodName;
    private final String paymentType;
    @DrawableRes
    private final int icon;

    //TODO make easier constructor with already known entities
    public PaymentModel(String paymentMethodId, String lastFourDigits,
                        String paymentMethodName,
                        String paymentType,
                        Integer accreditationTime,
                        String issuerName,
                        boolean moreThanOnePaymentMethod,
                        int icon) {

        this.paymentMethodId = paymentMethodId;
        this.lastFourDigits = lastFourDigits;
        this.paymentMethodName = paymentMethodName;
        this.paymentType = paymentType;
        this.accreditationTime = accreditationTime;
        this.issuerName = issuerName;
        this.moreThanOnePaymentMethod = moreThanOnePaymentMethod;
        this.icon = icon;
    }


    public String getPaymentType() {
        return paymentType;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public int getIcon() {
        return icon;
    }

    protected PaymentModel(Parcel in) {
        paymentMethodId = in.readString();
        lastFourDigits = in.readString();
        paymentMethodName = in.readString();
        paymentType = in.readString();
        if (in.readByte() == 0) {
            accreditationTime = null;
        } else {
            accreditationTime = in.readInt();
        }
        issuerName = in.readString();
        moreThanOnePaymentMethod = in.readByte() != 0;
        icon = in.readInt();
    }

    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(int size) {
            return new PaymentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(paymentMethodId);
        dest.writeString(lastFourDigits);
        dest.writeString(paymentMethodName);
        dest.writeString(paymentType);
        if (accreditationTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(accreditationTime);
        }
        dest.writeString(issuerName);
        dest.writeByte((byte) (moreThanOnePaymentMethod ? 1 : 0));
        dest.writeInt(icon);
    }
}
