package com.mercadopago.review_and_confirm;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.mercadopago.R;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;

import static com.mercadopago.util.TextUtils.isEmpty;

public class SummaryProviderImpl implements SummaryProvider {

    private final Context context;
    private final ReviewAndConfirmPreferences reviewAndConfirmPreferences;

    public SummaryProviderImpl(Context context, ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
        this.reviewAndConfirmPreferences = reviewAndConfirmPreferences;
        this.context = context;
    }

    @Override
    public String getSummaryProductsTitle() {
        String summaryProductTitle;

        if (!isEmpty(reviewAndConfirmPreferences.getProductTitle())) {
            summaryProductTitle = reviewAndConfirmPreferences.getProductTitle();
        } else {
            summaryProductTitle = context.getString(R.string.mpsdk_review_summary_product);
        }

        return summaryProductTitle;
    }

    @Override
    public int getDisclaimerTextColor() {
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
