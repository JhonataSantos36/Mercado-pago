package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.BuildConfig;
import com.mercadopago.R;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.lite.model.CardToken;
import com.mercadopago.lite.model.IdentificationType;
import com.mercadopago.lite.model.Installment;
import com.mercadopago.lite.model.Issuer;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.Token;
import com.mercadopago.lite.model.BankDeal;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.tracker.MPTrackingContext;

import java.math.BigDecimal;
import java.util.List;

public class GuessingCardProviderImpl implements GuessingCardProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;
    private final String publicKey;
    private MPTrackingContext trackingContext;

    public GuessingCardProviderImpl(Context context, String publicKey, String privateKey) {
        this.context = context;
        this.publicKey = publicKey;
        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey, privateKey);
    }

    @Override
    public MPTrackingContext getTrackingContext() {
        if (trackingContext == null) {
            trackingContext = new MPTrackingContext.Builder(context, publicKey)
                    .setVersion(BuildConfig.VERSION_NAME)
                    .build();
        }
        return trackingContext;
    }

    @Override
    public void getPaymentMethodsAsync(final TaggedCallback<List<PaymentMethod>> taggedCallback) {
        mercadoPago.getPaymentMethods(taggedCallback);
    }

    @Override
    public void createTokenAsync(CardToken cardToken, final TaggedCallback<Token> taggedCallback) {
        mercadoPago.createToken(cardToken, taggedCallback);
    }

    @Override
    public void getIssuersAsync(String paymentMethodId, String bin, final TaggedCallback<List<Issuer>> taggedCallback) {
        mercadoPago.getIssuers(paymentMethodId, bin, taggedCallback);
    }

    @Override
    public void getInstallmentsAsync(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final TaggedCallback<List<Installment>> taggedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, taggedCallback);
    }

    @Override
    public void getIdentificationTypesAsync(final TaggedCallback<List<IdentificationType>> taggedCallback) {
        mercadoPago.getIdentificationTypes(taggedCallback);
    }

    @Override
    public void getBankDealsAsync(final TaggedCallback<List<BankDeal>> taggedCallback) {
        mercadoPago.getBankDeals(taggedCallback);
    }


    @Override
    public String getMissingInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_installment_for_issuer);
    }

    @Override
    public String getMultipleInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_multiple_installments_for_issuer);
    }

    @Override
    public String getMissingPayerCostsErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_payer_cost);
    }

    @Override
    public String getMissingIdentificationTypesErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_identification_types);
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_public_key);
    }

    @Override
    public String getInvalidIdentificationNumberErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_number);
    }

    @Override
    public String getInvalidExpiryDateErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_expiry_date);
    }

    @Override
    public String getInvalidEmptyNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_empty_name);
    }

    @Override
    public String getSettingNotFoundForBinErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_setting_for_bin);
    }

    @Override
    public String getInvalidFieldErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_field);
    }

}
