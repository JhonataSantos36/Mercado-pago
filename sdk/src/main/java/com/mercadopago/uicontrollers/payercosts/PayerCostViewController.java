package com.mercadopago.uicontrollers.payercosts;

import android.view.View;

import com.mercadopago.model.PayerCost;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 12/5/16.
 */
public interface PayerCostViewController extends CustomViewController {
    void drawPayerCost(PayerCost payerCost);
    void drawPayerCostWithoutTotal(PayerCost payerCost);
    void setOnClickListener(View.OnClickListener listener);
    void setSmallTextSize();
}
