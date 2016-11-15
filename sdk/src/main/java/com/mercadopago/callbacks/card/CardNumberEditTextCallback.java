package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/13/16.
 */

public interface CardNumberEditTextCallback {
    void openKeyboard();
    void appendSpace(CharSequence s);
    void deleteChar(CharSequence s);
    void saveCardNumber(CharSequence s);
    void checkChangeErrorView();
    void toggleLineColorOnError(boolean toggle);
}
