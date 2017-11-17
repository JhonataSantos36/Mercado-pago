package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.controllers.CustomServicesHandler;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Installment;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.ApiUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by mromar on 4/28/17.
 */

public class InstallmentsProviderImpl implements InstallmentsProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;
    private final String merchantBaseUrl;
    private final String merchantDiscountBaseUrl;
    private final String merchantGetDiscountUri;
    private final Map<String, String> mDiscountAdditionalInfo;

    private ServicePreference servicePreference;

    public InstallmentsProviderImpl(Context context, String publicKey, String privateKey, String merchantBaseUrl,
                                    String merchantDiscountBaseUrl, String merchantGetDiscountUri, Map<String, String> discountAdditionalInfo) {
        this.context = context;
        this.merchantBaseUrl = merchantBaseUrl;
        this.merchantDiscountBaseUrl = merchantDiscountBaseUrl;
        this.merchantGetDiscountUri = merchantGetDiscountUri;
        this.mDiscountAdditionalInfo = discountAdditionalInfo;
        this.servicePreference = CustomServicesHandler.getInstance().getServicePreference();

        this.mercadoPago = new MercadoPagoServicesAdapter.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .build();
    }

    @Override
    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final OnResourcesRetrievedCallback<List<Installment>> onResourcesRetrievedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, new Callback<List<Installment>>() {
            @Override
            public void success(List<Installment> installments) {
                onResourcesRetrievedCallback.onSuccess(installments);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_INSTALLMENTS));
            }
        });
    }

    @Override
    public MercadoPagoError getNoInstallmentsFoundError() {
        String message = getStandardErrorMessage();
        String detail = context.getString(R.string.mpsdk_error_message_detail_no_installments);

        return new MercadoPagoError(message, detail, false);
    }

    @Override
    public MercadoPagoError getMultipleInstallmentsFoundForAnIssuerError() {
        String message = getStandardErrorMessage();
        String detail = context.getString(R.string.mpsdk_error_message_detail_multiple_installments);

        return new MercadoPagoError(message, detail, false);
    }

    @Override
    public MercadoPagoError getNoPayerCostFoundError() {
        String message = getStandardErrorMessage();
        String detail = context.getString(R.string.mpsdk_error_message_detail_no_payer_cost_found);

        return new MercadoPagoError(message, detail, false);
    }

    public String getStandardErrorMessage() {
        return context.getString(R.string.mpsdk_standard_error_message);
    }
}
