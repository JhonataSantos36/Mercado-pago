package com.mercadopago.review_and_confirm;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import com.mercadopago.R;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;

import static com.mercadopago.util.TextUtils.isEmpty;

public class SummaryProviderImpl implements SummaryProvider {

    private final Context context;

    public SummaryProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public String getSummaryProductsTitle() {
        ReviewAndConfirmPreferences reviewAndConfirmPreferences =
            CheckoutStore.getInstance().getReviewAndConfirmPreferences();
        String summaryProductTitle;

        if (!isEmpty(reviewAndConfirmPreferences.getProductTitle())) {
            summaryProductTitle = reviewAndConfirmPreferences.getProductTitle();
        } else {
            summaryProductTitle = context.getString(R.string.mpsdk_review_summary_product);
        }

        return summaryProductTitle;
    }

    @Override
    public int getDefaultTextColor() {
        return ContextCompat.getColor(context, R.color.mpsdk_summary_text_color);
    }

    @Override
    public String getSummaryShippingTitle() {
        return context.getString(R.string.mpsdk_review_summary_shipping);
    }

    @Override
    public int getDiscountTextColor() {
        return ContextCompat.getColor(context, R.color.mpsdk_summary_discount_color);
    }

    @Override
    public String getSummaryArrearTitle() {
        return context.getString(R.string.mpsdk_review_summary_arrear);
    }

    @Override
    public String getSummaryTaxesTitle() {
        return context.getString(R.string.mpsdk_review_summary_taxes);
    }

    @Override
    public String getSummaryDiscountsTitle() {
        return context.getString(R.string.mpsdk_review_summary_discounts);
    }

    @Override
    public int getDisclaimerTextColor() {
        ReviewAndConfirmPreferences reviewAndConfirmPreferences =
            CheckoutStore.getInstance().getReviewAndConfirmPreferences();
        int disclaimerTextColor;

        if (isEmpty(reviewAndConfirmPreferences.getDisclaimerTextColor())) {
            disclaimerTextColor = ContextCompat.getColor(context, R.color.mpsdk_default_disclaimer);
        } else {
            disclaimerTextColor = Color.parseColor(reviewAndConfirmPreferences.getDisclaimerTextColor());
        }

        return disclaimerTextColor;
    }

    @Override
    public String getSummaryChargesTitle() {
        return context.getString(R.string.mpsdk_review_summary_charges);
    }
}
