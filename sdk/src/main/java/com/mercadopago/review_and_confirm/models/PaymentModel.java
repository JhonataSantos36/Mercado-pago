package com.mercadopago.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;

public class PaymentModel implements Parcelable {

    public final String paymentMethodId;
    public final String lastFourDigits;
    public final Integer accreditationTime;
    public final String issuerName;
    public final boolean moreThanOnePaymentMethod;
    public final String paymentMethodName;
    private final String paymentType;
    public final long issuerId;

    public PaymentModel(PaymentMethod paymentMethod,
                        Token token,
                        Issuer issuer,
                        boolean moreThanOnePaymentMethod) {

        this.paymentMethodId = paymentMethod.getId();
        this.paymentMethodName = paymentMethod.getName();
        this.paymentType = paymentMethod.getPaymentTypeId();
        this.accreditationTime = paymentMethod.getAccreditationTime();
        //Token and issuer are not always available
        this.lastFourDigits = token != null ? token.getLastFourDigits() : null;
        this.issuerName = issuer != null ? issuer.getName() : null;
        this.issuerId = issuer != null ? issuer.getId() : 0L;
        this.moreThanOnePaymentMethod = moreThanOnePaymentMethod;
    }

    protected PaymentModel(Parcel in) {
        paymentMethodId = in.readString();
        lastFourDigits = in.readString();
        if (in.readByte() == 0) {
            accreditationTime = null;
        } else {
            accreditationTime = in.readInt();
        }
        issuerName = in.readString();
        moreThanOnePaymentMethod = in.readByte() != 0;
        paymentMethodName = in.readString();
        paymentType = in.readString();
        issuerId = in.readLong();
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

    public String getPaymentType() {
        return paymentType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(paymentMethodId);
        dest.writeString(lastFourDigits);
        if (accreditationTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(accreditationTime);
        }
        dest.writeString(issuerName);
        dest.writeByte((byte) (moreThanOnePaymentMethod ? 1 : 0));
        dest.writeString(paymentMethodName);
        dest.writeString(paymentType);
        dest.writeLong(issuerId);
    }
}
