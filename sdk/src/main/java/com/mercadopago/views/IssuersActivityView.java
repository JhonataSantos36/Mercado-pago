package com.mercadopago.views;

import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.Issuer;
import com.mercadopago.mvp.MvpView;

import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public interface IssuersActivityView extends MvpView {

    void showIssuers(List<Issuer> issuersList, OnSelectedCallback<Integer> onSelectedCallback);

    void showHeader();

    void showLoadingView();

    void stopLoadingView();

    void showError(MercadoPagoError error, String requestOrigin);

    void finishWithResult(Issuer issuer);
}
