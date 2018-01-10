package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.PaymentResultProvider;
import com.mercadopago.paymentresult.props.ReceiptProps;

/**
 * Created by vaserber on 04/12/2017.
 */

public class Receipt extends Component<ReceiptProps, Void> {

    public PaymentResultProvider paymentResultProvider;

    public Receipt(@NonNull ReceiptProps props, @NonNull ActionDispatcher dispatcher,
                   @NonNull final PaymentResultProvider paymentResultProvider) {
        super(props, dispatcher);
        this.paymentResultProvider = paymentResultProvider;
    }

    public String getDescription() {
        return paymentResultProvider.getReceiptDescription(props.receiptId);
    }
}
