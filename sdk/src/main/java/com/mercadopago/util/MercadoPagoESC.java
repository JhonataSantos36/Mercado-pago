package com.mercadopago.util;

import java.util.Set;

/**
 * Created by vaserber on 7/21/17.
 */

public interface MercadoPagoESC {

    String getESC(String cardId);

    boolean saveESC(String cardId, String value);

    void deleteESC(String cardId);

    void deleteAllESC();

    Set<String> getESCCardIds();

    boolean isESCEnabled();
}
