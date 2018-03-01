package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.card.CardExpiryDateEditTextCallback;

/**
 * Created by vaserber on 10/19/16.
 */

public class CardExpiryDateTextWatcher implements TextWatcher {

    private static final int MONTH_LENGTH = 2;
    private static final int YEAR_START_INDEX = 3;

    private CardExpiryDateEditTextCallback mEditTextCallback;

    public CardExpiryDateTextWatcher(CardExpiryDateEditTextCallback editTextCallback) {
        this.mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do something
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.checkOpenKeyboard();
        if (s.length() == 2 && before == 0) {
            mEditTextCallback.appendDivider();
        }
        if (s.length() == 2 && before == 1) {
            mEditTextCallback.deleteChar(s);
        }
        if (start <= MONTH_LENGTH) {
            CharSequence month = s;
            if (s.length() >= YEAR_START_INDEX) {
                month = s.subSequence(0, MONTH_LENGTH);
            }
            mEditTextCallback.saveExpiryMonth(month);
        } else {
            CharSequence year = s.subSequence(YEAR_START_INDEX, s.length());
            mEditTextCallback.saveExpiryYear(year);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == YEAR_START_INDEX) {
            mEditTextCallback.saveExpiryYear("");
        } else if (s.length() == 0) {
            mEditTextCallback.saveExpiryMonth("");
        }
        mEditTextCallback.changeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
    }
}
