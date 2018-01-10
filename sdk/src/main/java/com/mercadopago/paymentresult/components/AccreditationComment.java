package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;

/**
 * Created by vaserber on 11/13/17.
 */

public class AccreditationComment extends Component<AccreditationComment.Props, Void> {

    public AccreditationComment(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Props {

        public final String comment;

        public Props(@NonNull final String comment) {
            this.comment = comment;
        }

        public Props(@NonNull final Builder builder) {
            this.comment = builder.comment;
        }

        public Builder toBuilder() {
            return new Props.Builder()
                    .setComment(this.comment);
        }

        public static final class Builder {
            public String comment;

            public Builder setComment(String comment) {
                this.comment = comment;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }
}
