package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/19/16.
 */

public interface CardExpiryDateEditTextCallback {
    void checkOpenKeyboard();
    void saveExpiryMonth(CharSequence s);
    void saveExpiryYear(CharSequence s);
    void changeErrorView();
    void toggleLineColorOnError(boolean toggle);
    void appendDivider();
    void deleteChar(CharSequence s);
}
