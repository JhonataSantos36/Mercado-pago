package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.card.CardholderNameEditTextCallback;

/**
 * Created by vaserber on 10/19/16.
 */

public class CardholderNameTextWatcher implements TextWatcher {

    private final CardholderNameEditTextCallback mEditTextCallback;

    public CardholderNameTextWatcher(CardholderNameEditTextCallback editTextCallback) {
        mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.checkOpenKeyboard();
        mEditTextCallback.saveCardholderName(s.toString().toUpperCase());
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditTextCallback.changeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
    }
}
