package com.mercadopago.model;

/**
 * Created by mreverter on 2/2/17.
 */
public interface ReviewSubscriber {
    @Deprecated
    void changeRequired(Reviewable reviewable);

    void changeRequired(Integer resultCode);
}
