package com.mercadopago.callbacks.card;

/**
 * Created by mromar on 29/09/17.
 */

public interface TicketIdentificationNameEditTextCallback {
    void checkOpenKeyboard();

    void saveIdentificationName(CharSequence s);

    void changeErrorView();

    void toggleLineColorOnError(boolean toggle);
}
