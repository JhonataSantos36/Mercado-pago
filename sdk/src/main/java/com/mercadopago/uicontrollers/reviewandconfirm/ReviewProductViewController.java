package com.mercadopago.uicontrollers.reviewandconfirm;

import com.mercadopago.model.Item;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by vaserber on 11/10/16.
 */

public interface ReviewProductViewController extends CustomViewController {
    void drawProduct(int position, Item item, String currencyId);
}
