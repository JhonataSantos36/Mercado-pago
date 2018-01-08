package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.model.Instruction;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsContentProps {

    public final Instruction instruction;

    public InstructionsContentProps(@NonNull final Instruction instruction) {
        this.instruction = instruction;
    }

    public InstructionsContentProps(@NonNull final Builder builder) {
        this.instruction = builder.instruction;
    }

    public Builder toBuilder() {
        return new Builder()
                .setInstruction(this.instruction);
    }

    public static class Builder {

        public Instruction instruction;

        public Builder setInstruction(@NonNull Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public InstructionsContentProps build() {
            return new InstructionsContentProps(this);
        }
    }
}
