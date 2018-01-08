package com.mercadopago.paymentresult;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;

public class PaymentMethodProviderImpl implements PaymentMethodProvider {

    private final Context context;

    private static final String SDK_PREFIX = "mpsdk_";
    private static final String DEF_TYPE_DRAWABLE = "drawable";

    public PaymentMethodProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public Drawable getImage(PaymentMethod paymentMethod) {
        Drawable drawable = null;
        try {
            final @DrawableRes int image = getPaymentMethodIcon(context, paymentMethod.getId());
            drawable = ContextCompat.getDrawable(context, image);
        } catch (final Resources.NotFoundException e) {
            // Avoid crashes if the image doesn exist return empty default one.
            drawable = ContextCompat.getDrawable(context, R.drawable.mpsdk_none);
        }
        return drawable;
    }

    @Override
    public String getLastDigitsText() {
        return context.getString(R.string.mpsdk_ending_in);
    }

    @Override
    public String getAccountMoneyText() {
        return context.getString(R.string.mpsdk_account_money);
    }

    @Override
    public String getDisclaimer(String statementDescription) {
        String disclaimer = "";

        if (statementDescription != null && !statementDescription.isEmpty()) {
            disclaimer = String.format(context.getString(R.string.mpsdk_text_state_account_activity_congrats), statementDescription);
        }
        return disclaimer;
    }

    @DrawableRes
    private int getPaymentMethodIcon(Context context, String paymentMethodId) {
        int resource;
        paymentMethodId = SDK_PREFIX + paymentMethodId;
        try {
            resource = context.getResources().getIdentifier(paymentMethodId, DEF_TYPE_DRAWABLE, context.getPackageName());
        } catch (Exception e) {
            try {
                resource = context.getResources().getIdentifier(SDK_PREFIX + "bank", DEF_TYPE_DRAWABLE, context.getPackageName());
            } catch (Exception ex) {
                resource = 0;
            }
        }
        return resource;
    }
}
