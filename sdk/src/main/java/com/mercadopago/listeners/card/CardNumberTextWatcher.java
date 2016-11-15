package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.callbacks.card.CardNumberEditTextCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;

import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public class CardNumberTextWatcher implements TextWatcher {

    private PaymentMethodGuessingController mController;
    private PaymentMethodSelectionCallback mPaymentSelectionCallback;
    private CardNumberEditTextCallback mEditTextCallback;
    private String mBin;

    public CardNumberTextWatcher(PaymentMethodGuessingController controller,
                                 PaymentMethodSelectionCallback paymentSelectionCallback,
                                 CardNumberEditTextCallback editTextCallback) {
        this.mController = controller;
        this.mPaymentSelectionCallback = paymentSelectionCallback;
        this.mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.openKeyboard();
        mEditTextCallback.saveCardNumber(s.toString().replaceAll("\\s", ""));
        if (before == 0) {
            mEditTextCallback.appendSpace(s);
        }
        if (before == 1) {
            mEditTextCallback.deleteChar(s);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditTextCallback.checkChangeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
        if (mController == null) return;
        String number = s.toString().replaceAll("\\s", "");
        if (number.length() == MercadoPago.BIN_LENGTH - 1) {
            mPaymentSelectionCallback.onPaymentMethodCleared();
        } else if (number.length() >= MercadoPago.BIN_LENGTH) {
            mBin = number.subSequence(0, MercadoPago.BIN_LENGTH).toString();
            List<PaymentMethod> list = mController.guessPaymentMethodsByBin(mBin);
            mPaymentSelectionCallback.onPaymentMethodListSet(list);
        }
    }

}
