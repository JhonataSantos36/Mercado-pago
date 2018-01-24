package com.mercadopago.paymentresult;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.mercadopago.R;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.plugins.PaymentMethodPlugin;

import java.util.List;

public class PaymentMethodProviderImpl implements PaymentMethodProvider {

    private final Context context;

    private static final String SDK_PREFIX = "mpsdk_";
    private static final String DEF_TYPE_DRAWABLE = "drawable";

    public PaymentMethodProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public int getIconResource(PaymentMethod paymentMethod) {
        int icon;
        PaymentMethodPlugin paymentMethodPlugin = CheckoutStore.getInstance().getPaymentMethodPluginById(paymentMethod.getId());

        try {
            if (paymentMethodPlugin != null) {
                icon = paymentMethodPlugin.getPaymentMethodInfo(context).icon;
            } else {
                icon = getPaymentMethodIcon(context, paymentMethod.getId());
            }
        } catch (final Resources.NotFoundException e) {
            // Avoid crashes if the image doesn exist return empty default one.
            icon = R.drawable.mpsdk_none;
        }
        return icon;
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
