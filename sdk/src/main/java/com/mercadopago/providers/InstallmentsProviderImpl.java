package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.Installment;
import com.mercadopago.mvp.TaggedCallback;

import java.math.BigDecimal;
import java.util.List;

public class InstallmentsProviderImpl implements InstallmentsProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public InstallmentsProviderImpl(Context context, String publicKey, String privateKey) {
        this.context = context;
        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey, privateKey);
    }

    @Override
    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final TaggedCallback<List<Installment>> taggedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, taggedCallback);
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
