package com.mercadopago.uicontrollers.paymentmethods.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.model.Card;

/**
 * Created by mreverter on 28/6/16.
 */
public class PaymentMethodOnSelectionRow extends PaymentMethodOnView {

    protected Card mCard;

    public PaymentMethodOnSelectionRow(Context context, Card card) {
        mContext = context;
        mPaymentMethod = card.getPaymentMethod();
        mCard = card;
    }

    @Override
    protected String getLastFourDigits() {
        String lastFourDigits = "";
        if (mCard != null) {
            lastFourDigits = mCard.getLastFourDigits();
        }
        return lastFourDigits;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_payment_method_card, parent, attachToRoot);
        return mView;
    }
}
