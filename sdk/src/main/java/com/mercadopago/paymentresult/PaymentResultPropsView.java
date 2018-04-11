package com.mercadopago.paymentresult;

import android.support.annotation.NonNull;

import com.mercadopago.model.Instruction;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.mvp.MvpView;
import com.mercadopago.paymentresult.formatter.BodyAmountFormatter;
import com.mercadopago.paymentresult.formatter.HeaderTitleFormatter;

public interface PaymentResultPropsView extends MvpView {

    void setPropPaymentResult(@NonNull final PaymentResult paymentResult,
                              @NonNull final HeaderTitleFormatter formatter,
                              @NonNull final BodyAmountFormatter bodyAmountFormatter,
                              final boolean showLoading);

    void setPropInstruction(@NonNull final Instruction instruction,
                            @NonNull final HeaderTitleFormatter formatter,
                            final boolean showLoading,
                            @NonNull final String processingMode);

    void notifyPropsChanged();

}
