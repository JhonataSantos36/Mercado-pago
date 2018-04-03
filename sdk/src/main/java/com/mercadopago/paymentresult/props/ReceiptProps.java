package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

/**
 * Created by vaserber on 04/12/2017.
 */

public class ReceiptProps {

    public final Long receiptId;

    public ReceiptProps(@NonNull final Long receiptId) {
        this.receiptId = receiptId;
    }

    public ReceiptProps(@NonNull final Builder builder) {
        receiptId = builder.receiptId;
    }

    public Builder toBuilder() {
        return new Builder()
                .setReceiptId(receiptId);
    }

    public static class Builder {

        public Long receiptId;

        public Builder setReceiptId(Long receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public ReceiptProps build() {
            return new ReceiptProps(this);
        }
    }
}
