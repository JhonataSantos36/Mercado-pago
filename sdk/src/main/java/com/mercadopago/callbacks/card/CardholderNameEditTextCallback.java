package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/19/16.
 */

public interface CardholderNameEditTextCallback {
    void checkOpenKeyboard();
    void saveCardholderName(CharSequence s);
    void changeErrorView();
    void toggleLineColorOnError(boolean toggle);
}
