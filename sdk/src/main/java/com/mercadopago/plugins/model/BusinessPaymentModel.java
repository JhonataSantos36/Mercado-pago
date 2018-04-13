package com.mercadopago.plugins.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.mercadopago.components.PaymentMethodComponent;
import com.mercadopago.components.TotalAmount;
import com.mercadopago.model.Discount;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;

import java.math.BigDecimal;

public class BusinessPaymentModel implements Parcelable {

    public final BusinessPayment payment;
    private final Discount discount;
    private final PaymentMethod paymentMethod;
    private final PayerCost payerCost;
    private final String currencyId;
    private final BigDecimal amount;

    private final String lastFourDigits;

    public BusinessPaymentModel(final BusinessPayment payment,
                                final Discount discount,
                                final PaymentMethod paymentMethod,
                                final PayerCost payerCost,
                                final String currencyId,
                                final BigDecimal amount,
                                final String lastFourDigits) {
        this.payment = payment;
        this.discount = discount;
        this.paymentMethod = paymentMethod;
        this.payerCost = payerCost;
        this.currencyId = currencyId;
        this.amount = amount;
        this.lastFourDigits = lastFourDigits;
    }

    public PaymentMethodComponent.PaymentMethodProps getPaymentMethodProps() {
        TotalAmount.TotalAmountProps totalAmountProps = new TotalAmount.TotalAmountProps(currencyId, amount, payerCost, discount);
        return new PaymentMethodComponent.PaymentMethodProps(paymentMethod, lastFourDigits, payment.getPaymentMethodDisclaimer(), totalAmountProps);
    }

    protected BusinessPaymentModel(Parcel in) {
        payment = in.readParcelable(BusinessPayment.class.getClassLoader());
        discount = in.readParcelable(Discount.class.getClassLoader());
        paymentMethod = in.readParcelable(PaymentMethod.class.getClassLoader());
        payerCost = in.readParcelable(PayerCost.class.getClassLoader());
        currencyId = in.readString();
        amount = new BigDecimal(in.readString());
        lastFourDigits = in.readString();
    }

    public static final Creator<BusinessPaymentModel> CREATOR = new Creator<BusinessPaymentModel>() {
        @Override
        public BusinessPaymentModel createFromParcel(Parcel in) {
            return new BusinessPaymentModel(in);
        }

        @Override
        public BusinessPaymentModel[] newArray(int size) {
            return new BusinessPaymentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(payment, flags);
        dest.writeParcelable(discount, flags);
        dest.writeParcelable(paymentMethod, flags);
        dest.writeParcelable(payerCost, flags);
        dest.writeString(currencyId);
        dest.writeString(amount.toString());
        dest.writeString(lastFourDigits);
    }
}
