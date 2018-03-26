package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.model.InstructionActionInfo;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActionsProps {

    public final List<InstructionActionInfo> instructionActions;

    public InstructionsActionsProps(@NonNull final List<InstructionActionInfo> instructionActions) {
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
        public List<InstructionActionInfo> instructionsActions;

        public Builder setInstructionsActions(List<InstructionActionInfo> instructionsActions) {
            this.instructionsActions = instructionsActions;
            return this;
        }

        public InstructionsActionsProps build() {
            return new InstructionsActionsProps(this);
        }
    }
}
