package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.model.Installment;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.util.MercadoPagoESC;
import com.mercadopago.util.MercadoPagoESCImpl;

import java.math.BigDecimal;
import java.util.List;

public class CardVaultProviderImpl implements CardVaultProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;
    private final MercadoPagoESC mercadoPagoESC;

    public CardVaultProviderImpl(Context context, String publicKey, String privateKey, boolean escEnabled) {
        this.context = context;

        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey, privateKey);

        mercadoPagoESC = new MercadoPagoESCImpl(context, escEnabled);
    }

    @Override
    public String getMultipleInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_multiple_installments_for_issuer);
    }

    @Override
    public String getMissingInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_installment_for_issuer);
    }

    @Override
    public String getMissingPayerCostsErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_payer_cost);
    }

    @Override
    public String getMissingAmountErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_amount);
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_public_key);
    }

    @Override
    public String getMissingSiteErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_site);
    }

    @Override
    public void getInstallmentsAsync(final String bin,
                                     final Long issuerId,
                                     final String paymentMethodId,
                                     final BigDecimal amount,
                                     final TaggedCallback<List<Installment>> taggedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, taggedCallback);
    }

    @Override
    public void createESCTokenAsync(SavedESCCardToken escCardToken,
                                    final TaggedCallback<Token> taggedCallback) {
        mercadoPago.createToken(escCardToken, taggedCallback);
    }

    @Override
    public String findESCSaved(String cardId) {
        return mercadoPagoESC.getESC(cardId);
    }

    @Override
    public void deleteESC(String cardId) {
        mercadoPagoESC.deleteESC(cardId);
    }
}
