package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.lite.model.InstructionAction;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsAction extends Component<InstructionsAction.Prop, Void> {

    public InstructionsAction(@NonNull final Prop props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Prop {

        public final InstructionAction instructionAction;

        public Prop(@NonNull final InstructionAction instructionAction) {
            this.instructionAction = instructionAction;
        }

        public Prop(@NonNull final Builder builder) {
            instructionAction = builder.instructionAction;
        }

        public Builder toBuilder() {
            return new Prop.Builder()
                    .setInstructionAction(instructionAction);
        }

        public static final class Builder {
            public InstructionAction instructionAction;

            public Builder setInstructionAction(InstructionAction instructionAction) {
                this.instructionAction = instructionAction;
                return this;
            }

            public Prop build() {
                return new Prop(this);
            }
        }
    }
}
