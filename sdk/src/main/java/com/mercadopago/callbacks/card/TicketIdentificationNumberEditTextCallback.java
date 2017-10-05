package com.mercadopago.callbacks.card;

/**
 * Created by mromar on 29/09/17.
 */

public interface TicketIdentificationNumberEditTextCallback {
    void checkOpenKeyboard();

    void saveIdentificationNumber(CharSequence s);

    void changeErrorView();

    void toggleLineColorOnError(boolean toggle);
}
