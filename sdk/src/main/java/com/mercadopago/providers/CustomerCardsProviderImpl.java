package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.Customer;
import com.mercadopago.mvp.TaggedCallback;

public class CustomerCardsProviderImpl implements CustomerCardsProvider {

    private final Context context;
    private final String merchantAccessToken;
    private final String merchantBaseUrl;
    private final String merchantGetCustomerUri;

    public CustomerCardsProviderImpl(Context context, String merchantAccessToken, String merchantBaseUrl, String merchantGetCustomerUri) {
        this.context = context;
        this.merchantAccessToken = merchantAccessToken;
        this.merchantBaseUrl = merchantBaseUrl;
        this.merchantGetCustomerUri = merchantGetCustomerUri;
    }

    @Override
    public void getCustomer(final TaggedCallback<Customer> taggedCallback) {
        MerchantServer.getCustomer(context, merchantBaseUrl, merchantGetCustomerUri, merchantAccessToken, taggedCallback);
    }

    @Override
    public String getLastDigitsLabel() {
        return context.getString(R.string.mpsdk_last_digits_label);
    }

    @Override
    public String getConfirmPromptYes() {
        return context.getString(R.string.mpsdk_confirm_prompt_yes);
    }

    @Override
    public String getConfirmPromptNo() {
        return context.getString(R.string.mpsdk_confirm_prompt_no);
    }

    @Override
    public int getIconDialogAlert() {
        return android.R.drawable.ic_dialog_alert;
    }
}
