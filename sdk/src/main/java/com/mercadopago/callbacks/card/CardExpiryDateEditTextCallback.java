package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/19/16.
 */

public interface CardExpiryDateEditTextCallback {
    void openKeyboard();
    void saveExpiryMonth(CharSequence s);
    void saveExpiryYear(CharSequence s);
    void checkChangeErrorView();
    void toggleLineColorOnError(boolean toggle);
    void appendDivider();
    void deleteChar(CharSequence s);
}
