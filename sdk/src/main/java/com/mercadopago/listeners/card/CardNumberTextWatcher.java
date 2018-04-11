package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.callbacks.card.CardNumberEditTextCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.model.Bin;
import com.mercadopago.model.PaymentMethod;

import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public class CardNumberTextWatcher implements TextWatcher {

    private final PaymentMethodGuessingController mController;
    private final PaymentMethodSelectionCallback mPaymentSelectionCallback;
    private final CardNumberEditTextCallback mEditTextCallback;
    private String mBin;

    public CardNumberTextWatcher(PaymentMethodGuessingController controller,
                                 PaymentMethodSelectionCallback paymentSelectionCallback,
                                 CardNumberEditTextCallback editTextCallback) {
        mController = controller;
        mPaymentSelectionCallback = paymentSelectionCallback;
        mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do something
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.checkOpenKeyboard();
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
        mEditTextCallback.changeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
        if (mController == null) return;
        String number = s.toString().replaceAll("\\s", "");
        if (number.length() == Bin.BIN_LENGTH - 1) {
            mPaymentSelectionCallback.onPaymentMethodCleared();
        } else if (number.length() == Bin.BIN_LENGTH) {
            mBin = number.subSequence(0, Bin.BIN_LENGTH).toString();
            List<PaymentMethod> list = mController.guessPaymentMethodsByBin(mBin);
            mPaymentSelectionCallback.onPaymentMethodListSet(list, mBin);
        }
    }

}
