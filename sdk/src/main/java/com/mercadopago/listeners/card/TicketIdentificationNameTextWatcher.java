package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.card.TicketIdentificationNameEditTextCallback;

/**
 * Created by mromar on 29/09/17.
 */

public class TicketIdentificationNameTextWatcher implements TextWatcher {

    private TicketIdentificationNameEditTextCallback mEditTextCallback;

    public TicketIdentificationNameTextWatcher(TicketIdentificationNameEditTextCallback editTextCallback) {
        this.mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do something
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.checkOpenKeyboard();
        mEditTextCallback.saveIdentificationName(s.toString().toUpperCase());
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditTextCallback.changeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
    }
}
