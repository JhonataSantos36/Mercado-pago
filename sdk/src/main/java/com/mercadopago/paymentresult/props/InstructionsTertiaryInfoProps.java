package com.mercadopago.paymentresult.props;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsTertiaryInfoProps {

    public final List<String> tertiaryInfo;

    public InstructionsTertiaryInfoProps(@NonNull final List<String> tertiaryInfo) {
        this.tertiaryInfo = tertiaryInfo;
    }

    public InstructionsTertiaryInfoProps(@NonNull final Builder builder) {
        tertiaryInfo = builder.tertiaryInfo;
    }

    public Builder toBuilder() {
        return new InstructionsTertiaryInfoProps.Builder()
                .setTertiaryInfo(tertiaryInfo);
    }

    public static final class Builder {
        public List<String> tertiaryInfo;

        public Builder setTertiaryInfo(List<String> tertiaryInfo) {
            this.tertiaryInfo = tertiaryInfo;
            return this;
        }

        public InstructionsTertiaryInfoProps build() {
            return new InstructionsTertiaryInfoProps(this);
        }
    }
}
