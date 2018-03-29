package com.mercadopago.uicontrollers.issuers;

import android.view.View;

import com.mercadopago.lite.model.Issuer;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by vaserber on 10/11/16.
 */

public interface IssuersViewController extends CustomViewController {
    void drawIssuer(Issuer issuer);

    void setOnClickListener(View.OnClickListener listener);
}
