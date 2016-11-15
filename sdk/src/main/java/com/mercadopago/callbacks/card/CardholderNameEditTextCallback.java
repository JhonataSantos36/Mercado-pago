package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/19/16.
 */

public interface CardholderNameEditTextCallback {
    void openKeyboard();
    void saveCardholderName(CharSequence s);
    void checkChangeErrorView();
    void toggleLineColorOnError(boolean toggle);
}
