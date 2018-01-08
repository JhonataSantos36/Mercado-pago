package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import com.mercadopago.model.InstructionReference;

import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsReferencesProps {

    public final String title;
    public final List<InstructionReference> references;

    public InstructionsReferencesProps(final String title, final List<InstructionReference> references) {
        this.title = title;
        this.references = references;
    }

    public InstructionsReferencesProps(@NonNull final Builder builder) {
        this.title = builder.title;
        this.references = builder.references;
    }

    public Builder toBuilder() {
        return new InstructionsReferencesProps.Builder()
                .setTitle(this.title)
                .setReferences(this.references);
    }

    public static class Builder {
        public String title;
        public List<InstructionReference> references;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setReferences(List<InstructionReference> references) {
            this.references = references;
            return this;
        }

        public InstructionsReferencesProps build() {
            return new InstructionsReferencesProps(this);
        }
    }
}
