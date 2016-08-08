package com.mercadopago.uicontrollers.paymentmethods.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;

/**
 * Created by mreverter on 12/5/16.
 */
public class PaymentMethodCardEditableRow extends PaymentMethodCard {

    protected Token mToken;

    public PaymentMethodCardEditableRow(Context context, PaymentMethod paymentMethod, Token token) {
        mContext = context;
        mPaymentMethod = paymentMethod;
        mToken = token;
    }


    @Override
    protected String getLastFourDigits() {
        String lastFourDigits = "";
        if (mToken != null) {
            lastFourDigits = mToken.getLastFourDigits();
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
