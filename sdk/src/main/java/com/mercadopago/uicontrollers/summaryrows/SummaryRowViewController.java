package com.mercadopago.uicontrollers.summaryrows;

import android.view.View;

import com.mercadopago.model.SummaryRow;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by mromar on 9/11/17.
 */

public interface SummaryRowViewController extends CustomViewController {
    void drawSummaryRow(SummaryRow summaryRow);

    void setOnClickListener(View.OnClickListener listener);
}
