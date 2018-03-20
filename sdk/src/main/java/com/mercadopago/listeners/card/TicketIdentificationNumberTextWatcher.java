package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.card.TicketIdentificationNumberEditTextCallback;

/**
 * Created by mromar on 29/09/17.
 */

public class TicketIdentificationNumberTextWatcher implements TextWatcher {

    private final TicketIdentificationNumberEditTextCallback mEditTextCallback;

    public TicketIdentificationNumberTextWatcher(TicketIdentificationNumberEditTextCallback editTextCallback) {
        mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do something
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.checkOpenKeyboard();
        mEditTextCallback.saveIdentificationNumber(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditTextCallback.changeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
    }
}
