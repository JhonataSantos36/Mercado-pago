package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.card.CardSecurityCodeEditTextCallback;

/**
 * Created by vaserber on 10/20/16.
 */

public class CardSecurityCodeTextWatcher implements TextWatcher {

    private final CardSecurityCodeEditTextCallback mEditTextCallback;

    public CardSecurityCodeTextWatcher(CardSecurityCodeEditTextCallback editTextCallback) {
        mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.checkOpenKeyboard();
        mEditTextCallback.saveSecurityCode(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditTextCallback.changeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
    }
}
