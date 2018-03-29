package com.mercadopago.paymentresult;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.util.ResourceUtil;

public class PaymentMethodProviderImpl implements PaymentMethodProvider {

    private final Context context;

    public PaymentMethodProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public int getIconResource(PaymentMethod paymentMethod) {
        return ResourceUtil.getIconResource(context, paymentMethod.getId());
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
}
