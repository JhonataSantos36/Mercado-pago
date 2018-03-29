package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.lite.model.InstructionAction;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActionsProps {

    public final List<InstructionAction> instructionActions;

    public InstructionsActionsProps(@NonNull final List<InstructionAction> instructionActions) {
        this.instructionActions = instructionActions;
    }

    public InstructionsActionsProps(@NonNull Builder builder) {
        instructionActions = builder.instructionsActions;
    }

    public Builder toBuilder() {
        return new InstructionsActionsProps.Builder()
                .setInstructionsActions(instructionActions);
    }

    public static class Builder {
        public List<InstructionAction> instructionsActions;

        public Builder setInstructionsActions(List<InstructionAction> instructionsActions) {
            this.instructionsActions = instructionsActions;
            return this;
        }

        public InstructionsActionsProps build() {
            return new InstructionsActionsProps(this);
        }
    }
}
