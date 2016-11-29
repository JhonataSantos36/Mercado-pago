package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/21/16.
 */

public interface CardIdentificationNumberEditTextCallback {
    void checkOpenKeyboard();
    void saveIdentificationNumber(CharSequence s);
    void changeErrorView();
    void toggleLineColorOnError(boolean toggle);
}
