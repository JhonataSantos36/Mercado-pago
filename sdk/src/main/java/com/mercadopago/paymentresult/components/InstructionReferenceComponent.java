package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.model.InstructionReference;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionReferenceComponent extends Component<InstructionReferenceComponent.Props, Void> {


    public InstructionReferenceComponent(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Props {

        public final InstructionReference reference;

        public Props(@NonNull InstructionReference reference) {
            this.reference = reference;
        }

        public Props(final Builder builder) {
            this.reference = builder.reference;
        }

        public Builder toBuilder(){
            return new Props.Builder()
                    .setReference(this.reference);
        }

        public static class Builder {
            public InstructionReference reference;

            public Builder setReference(@NonNull InstructionReference reference) {
                this.reference = reference;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }
}
