package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

/**
 * Created by vaserber on 27/11/2017.
 */

public class BodyErrorProps {

    public final String status;
    public final String statusDetail;
    public final String paymentMethodName;

    public BodyErrorProps(@NonNull final Builder builder) {
        status = builder.status;
        statusDetail = builder.statusDetail;
        paymentMethodName = builder.paymentMethodName;
    }

    public Builder toBuilder() {
        return new Builder()
                .setStatus(status)
                .setStatusDetail(statusDetail)
                .setPaymentMethodName(paymentMethodName);
    }

    public static class Builder {

        public String status;
        public String statusDetail;
        public String paymentMethodName;

        public Builder setStatus(@NonNull final String status) {
            this.status = status;
            return this;
        }

        public Builder setStatusDetail(@NonNull final String statusDetail) {
            this.statusDetail = statusDetail;
            return this;
        }

        public Builder setPaymentMethodName(String paymentMethodName) {
            this.paymentMethodName = paymentMethodName;
            return this;
        }

        public BodyErrorProps build() {
            return new BodyErrorProps(this);
        }
    }
}
