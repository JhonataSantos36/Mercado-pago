package com.mercadopago.paymentresult;

import android.support.annotation.NonNull;

import com.mercadopago.model.Instruction;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.mvp.MvpView;

public interface PaymentResultPropsView extends MvpView {

    void setPropPaymentResult(@NonNull final String currencyId,
                              @NonNull final PaymentResult paymentResult,
                              final boolean showLoading);

    void setPropInstruction(@NonNull final Instruction instruction,
                            @NonNull final String processingModeString,
                            final boolean showLoading);

    void notifyPropsChanged();

}
