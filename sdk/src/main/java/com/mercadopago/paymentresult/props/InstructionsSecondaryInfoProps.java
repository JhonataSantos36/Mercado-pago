package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsSecondaryInfoProps {

    public final List<String> secondaryInfo;

    public InstructionsSecondaryInfoProps(@NonNull final List<String> secondaryInfo) {
        this.secondaryInfo = secondaryInfo;
    }

    public InstructionsSecondaryInfoProps(@NonNull final Builder builder) {
        secondaryInfo = builder.secondaryInfo;
    }

    public Builder toBuilder() {
        return new InstructionsSecondaryInfoProps.Builder()
                .setSecondaryInfo(secondaryInfo);
    }

    public static final class Builder {
        public List<String> secondaryInfo;

        public Builder setSecondaryInfo(List<String> secondaryInfo) {
            this.secondaryInfo = secondaryInfo;
            return this;
        }

        public InstructionsSecondaryInfoProps build() {
            return new InstructionsSecondaryInfoProps(this);
        }
    }
}
