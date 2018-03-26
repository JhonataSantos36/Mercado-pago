package com.mercadopago.review_and_confirm;

import com.mercadopago.mvp.ResourcesProvider;

/**
 * Created by mromar on 3/5/18.
 */

public interface SummaryProvider extends ResourcesProvider {

    String getSummaryProductsTitle();
    int getDefaultTextColor();
    String getSummaryShippingTitle();
    int getDiscountTextColor();
    String getSummaryArrearTitle();
    String getSummaryTaxesTitle();
    String getSummaryDiscountsTitle();
    int getDisclaimerTextColor();
    String getSummaryChargesTitle();


}
