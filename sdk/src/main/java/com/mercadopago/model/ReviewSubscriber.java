package com.mercadopago.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by mreverter on 2/2/17.
 */
public interface ReviewSubscriber {
    @Deprecated
    void changeRequired(Reviewable reviewable);

    void changeRequired(Integer resultCode, @Nullable Bundle data);
}
