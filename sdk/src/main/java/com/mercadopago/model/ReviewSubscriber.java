package com.mercadopago.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by mreverter on 2/2/17.
 */
public interface ReviewSubscriber {
    void changeRequired(Integer resultCode, @Nullable Bundle data);
}
