package com.mercadopago.providers;

import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Installment;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mromar on 4/28/17.
 */

public interface InstallmentsProvider extends ResourcesProvider {

    void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final OnResourcesRetrievedCallback<List<Installment>> onResourcesRetrievedCallback);

    MercadoPagoError getNoInstallmentsFoundError();

    MercadoPagoError getMultipleInstallmentsFoundForAnIssuerError();

    MercadoPagoError getNoPayerCostFoundError();
}
