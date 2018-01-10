package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.model.InstructionActionInfo;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsAction extends Component<InstructionsAction.Prop, Void> {

    public InstructionsAction(@NonNull final Prop props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Prop {

        public final InstructionActionInfo instructionActionInfo;

        public Prop(@NonNull final InstructionActionInfo instructionActionInfo) {
            this.instructionActionInfo = instructionActionInfo;
        }

        public Prop(@NonNull final Builder builder) {
            this.instructionActionInfo = builder.instructionActionInfo;
        }

        public Builder toBuilder() {
            return new Prop.Builder()
                    .setInstructionActionInfo(this.instructionActionInfo);
        }

        public static final class Builder {
            public InstructionActionInfo instructionActionInfo;

            public Builder setInstructionActionInfo(InstructionActionInfo instructionActionInfo) {
                this.instructionActionInfo = instructionActionInfo;
                return this;
            }

            public Prop build() {
                return new Prop(this);
            }
        }
    }
}
