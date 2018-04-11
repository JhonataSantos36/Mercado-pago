package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.model.Instruction;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsProps {

    public final Instruction instruction;
    public final String processingMode;

    public InstructionsProps(@NonNull final Instruction instruction, @NonNull final String processingMode) {
        this.instruction = instruction;
        this.processingMode = processingMode;
    }

    public InstructionsProps(@NonNull final Builder builder) {
        instruction = builder.instruction;
        processingMode = builder.processingMode;
    }

    public Builder toBuilder() {
        return new Builder()
                .setInstruction(instruction)
                .setProcessingMode(processingMode);
    }

    public static class Builder {

        public Instruction instruction;
        public String processingMode;

        public Builder setInstruction(@NonNull Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder setProcessingMode(String processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public InstructionsProps build() {
            return new InstructionsProps(this);
        }
    }
}
